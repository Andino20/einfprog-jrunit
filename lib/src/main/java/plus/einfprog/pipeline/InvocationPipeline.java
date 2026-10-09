package plus.einfprog.pipeline;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.exception.InvocationTimeoutException;
import plus.einfprog.exception.TargetInvocationException;
import plus.einfprog.pipeline.hook.AfterHook;
import plus.einfprog.pipeline.hook.BeforeHook;
import plus.einfprog.pipeline.hook.ExceptionHook;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * <p>This class represents a collection of {@link Pipeline} objects
 * that are executed each time a method or constructor should be invoked on a target class or object,
 * either via proxy objects or {@link plus.einfprog.reflection.Reflected}.
 * These pipelines contain hooks which can be configured to intercept and inspect method calls, exceptions, and return values.
 * </p>
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
 * <p>If the execution of a target method takes more time than the pre-configured timeout duration, it is
 * interrupted and a {@link InvocationTimeoutException} is thrown.
 * This timeout can be configured via {@link plus.einfprog.Settings} during library initialization.</p>
 *
 * @param before The pipeline to be executed before the target method is invoked.
 * @param after The pipeline to be executed after the target method has been invoked if and only if there was no internal exception.
 * @param exception The pipeline to be executed if and only if an exception occurs during method invocation.
 *
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

    /**
     * Initializes the before, after, and exception pipelines without hooks.
     * @return An {@link InvocationPipeline} object with empty pipelines.
     */
    public static InvocationPipeline empty() {
        return new InvocationPipeline(
                new Pipeline<>(beforeHook -> beforeHook::apply),
                new Pipeline<>(afterHook -> afterHook::apply),
                new Pipeline<>(exceptionHook -> exceptionHook::apply));
    }

    public InvocationResult run(Invocation invocation) {
        long timeout = EinfprogJRunit.getContext().settings().getTimeout();
        TimeUnit unit = EinfprogJRunit.getContext().settings().getTimeoutUnit();

        invocation = before().run(invocation);
        Object returnValue = invokeWithTimeout(invocation, timeout, unit);
        return after().run(InvocationResult.from(invocation, returnValue));
    }

    private Object invokeWithTimeout(Invocation invocation, long timeout, TimeUnit unit) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Object> returnValue = executor.submit(() -> invoke(invocation));

        try {
            return returnValue.get(timeout, unit);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException r) {
                throw r;
            } else {
                throw new RuntimeException("an unexpected error occurred", e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("an unexpected error occurred", e);
        } catch (TimeoutException e) {
            throw new InvocationTimeoutException(e);
        } finally {
            executor.shutdownNow();
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
