package plus.einfprog.pipeline;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.intercepter.AfterHook;
import plus.einfprog.pipeline.intercepter.BeforeHook;
import plus.einfprog.pipeline.intercepter.ExceptionHook;
import plus.einfprog.util.PrependedList;

import java.lang.invoke.WrongMethodTypeException;
import java.util.concurrent.*;

/**
 * <p>This class represents a collection of {@link Pipeline} objects
 * that are executed each time a method should be invoked on a target class or object,
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
 * interrupted and a {@link RuntimeTimeoutException} is thrown.
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
 * @see RuntimeTimeoutException
 */
public record InvocationPipeline(Pipeline<MethodCall, BeforeHook> before,
                                 Pipeline<MethodCallResult, AfterHook> after,
                                 Pipeline<Throwable, ExceptionHook> exception) {

    /**
     * Initializes the before, after, and exception pipelines without hooks.
     * @return An {@link InvocationPipeline} object with empty pipelines.
     */
    public static InvocationPipeline empty() {
        return new InvocationPipeline(
                new Pipeline<>(beforeHook -> beforeHook::intercept),
                new Pipeline<>(afterHook -> afterHook::intercept),
                new Pipeline<>(exceptionHook -> exceptionHook::intercept));
    }

    public MethodCallResult run(MethodCall call) {
        long timeout = EinfprogJRunit.getContext().settings().getTimeout();
        TimeUnit unit = EinfprogJRunit.getContext().settings().getTimeoutUnit();

        call = before().run(call);
        Object returnValue = invokeWithTimeout(call, timeout, unit);
        return after().run(MethodCallResult.from(call, returnValue));
    }

    private Object invokeWithTimeout(MethodCall call, long timeout, TimeUnit unit) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Object> returnValue = executor.submit(() -> invoke(call));

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
            throw new RuntimeTimeoutException(e);
        } finally {
            executor.shutdownNow();
        }
    }

    private Object invoke(MethodCall call) {
        try {
            return call.methodHandle().invokeWithArguments(call.isStatic() ?
                    call.arguments() :
                    new PrependedList<>(call.target(), call.arguments()));
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new RuntimeException("an unexpected error occurred", e);
        } catch (Throwable e) {
            Throwable t = exception().run(e);
            throw new TargetInvocationException("an exception occurred while invoking a target method", t);
        }
    }

}
