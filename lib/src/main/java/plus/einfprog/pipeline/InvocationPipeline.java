package plus.einfprog.pipeline;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.exception.InvocationTimeoutException;
import plus.einfprog.exception.TargetInvocationException;
import plus.einfprog.pipeline.hook.*;
import plus.einfprog.proxy.ProxyUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.*;

/**
 * <p>This class represents a collection of {@link Pipeline} objects
 * that are executed each time a method or constructor should be invoked on a target class or object,
 * either via proxy objects or {@link plus.einfprog.reflection.Reflected}.
 * These pipelines contain hooks which can be configured to intercept and inspect method calls, exceptions, and return values.
 * </p>
 *
 * <p>Before any hook runs, {@link #run(InvocationRequest)} unwraps proxies in the {@link InvocationRequest}
 * and resolves it into an {@link Invocation} (see {@link InvocationResolver}). Hooks therefore only see
 * target objects and a resolved executable.</p>
 *
 * <p>There are three pipelines that are executed at different steps of the invocation process:</p>
 * <ul>
 *     <li><strong>Before</strong> - Intercepts method calls before they are invoked on the target object or class.</li>
 *     <li><strong>After</strong> - Intercepts return values after the method has been invoked without throwing an exception.</li>
 *     <li><strong>Exception</strong> - Intercepts exceptions thrown during method invocation.</li>
 * </ul>
 *
 * <p>Each pipeline can be interrupted prematurely is not guaranteed to execute each hook if a previous hook throws an exception.</p>
 *
 * <p>If the targeted method throws an exception internally, that exception is wrapped inside a {@link TargetInvocationException}
 * and can be retrieved by {@code getCause()}.</p>
 *
 * <p>The target method or constructor is invoked on a separate thread of a shared {@link TimeoutRunner}.
 * If it takes more time than the pre-configured timeout duration, an {@link InvocationTimeoutException} is thrown
 * and the thread is interrupted.
 * This timeout can be configured via {@link plus.einfprog.Settings} during library initialization.</p>
 *
 * @param before    The pipeline to be executed before the target method is invoked.
 * @param after     The pipeline to be executed after the target method has been invoked if and only if there was no internal exception.
 * @param exception The pipeline to be executed if and only if an exception occurs during method invocation.
 * @see Pipeline
 * @see BeforeHook
 * @see AfterHook
 * @see ExceptionHook
 * @see TargetInvocationException
 * @see InvocationTimeoutException
 */
public record InvocationPipeline(Pipeline<Invocation, BeforeHook> before,
                                 Pipeline<InvocationResult, AfterHook> after,
                                 Pipeline<Throwable, ExceptionHook> exception) {

    private static final TimeoutRunner TIMEOUT_RUNNER = new TimeoutRunner("einfprog-invocation");

    /**
     * Initializes the before, after, and exception pipelines without hooks.
     *
     * @return An {@link InvocationPipeline} object with empty pipelines.
     */
    public static InvocationPipeline empty() {
        return new InvocationPipeline(
                new Pipeline<>(beforeHook -> beforeHook::apply),
                new Pipeline<>(afterHook -> afterHook::apply),
                new Pipeline<>(exceptionHook -> exceptionHook::apply));
    }

    /**
     * Unwraps proxies in the request and resolves it, runs the before hooks, invokes the resolved executable
     * with the configured timeout, wraps the return value into a proxy if the requested return type is one,
     * and runs the after hooks.
     *
     * @param request the requested invocation, possibly containing proxies
     * @return the result after all after hooks have been applied
     * @throws plus.einfprog.exception.TargetNotFoundException if no matching method or constructor exists
     * @throws TargetInvocationException                       if the target method or constructor threw an exception
     * @throws InvocationTimeoutException                      if the invocation didn't finish within the configured timeout
     */
    public InvocationResult run(InvocationRequest request) {
        List<Class<?>> unwrappedParameterTypes = request.argumentTypes().stream().<Class<?>>map(ProxyUtil::unwrapClass).toList();
        List<Object> unwrappedArguments = request.arguments().stream().map(ProxyUtil::unwrap).toList();
        Class<?> unwrappedReturnType = ProxyUtil.unwrapClass(request.returnType());

        Invocation invocation = InvocationResolver.resolve(request
                .withArgumentTypes(unwrappedParameterTypes)
                .withArguments(unwrappedArguments)
                .withReturnType(unwrappedReturnType));

        invocation = before().run(invocation);
        Object returnValue = invokeWithTimeout(invocation);
        InvocationResult result = after().run(InvocationResult.from(invocation, returnValue));
        return result.withReturnValue(ProxyAutoWrapper.wrapReturnValue(request.returnType(), result.returnValue()));
    }

    private Object invokeWithTimeout(Invocation invocation) {
        long timeout = EinfprogJRunit.getContext().settings().getTimeout();
        TimeUnit unit = EinfprogJRunit.getContext().settings().getTimeoutUnit();
        try {
            return TIMEOUT_RUNNER.submit(timeout, unit, () -> invoke(invocation));
        } catch (TimeoutException e) {
            throw new InvocationTimeoutException(e);
        }
    }

    private Object invoke(Invocation invocation) {
        Object[] args = invocation.arguments().toArray();
        try {
            return switch (invocation.executable()) {
                case Method m -> m.invoke(invocation.target(), args);
                case Constructor<?> c -> c.newInstance(args);
                case null -> throw new IllegalStateException("executable of invocation was not resolved");
            };
        } catch (InvocationTargetException e) {
            Throwable t = exception().run(e.getCause());
            throw new TargetInvocationException("an exception occurred while invoking a target method", t);
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            throw new RuntimeException("an unexpected error occurred", e);
        }
    }

}
