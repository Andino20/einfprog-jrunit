package plus.einfprog.pipeline;

import plus.einfprog.exception.InvocationTimeoutException;
import plus.einfprog.exception.TargetInvocationException;
import plus.einfprog.pipeline.hook.AfterHook;
import plus.einfprog.pipeline.hook.BeforeHook;
import plus.einfprog.pipeline.hook.ExceptionHook;
import plus.einfprog.pipeline.hook.InvocationResolver;

/**
 * <p>This interface represents a collection of {@link Pipeline} objects
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
 * <p>Each pipeline can be interrupted prematurely and is not guaranteed to execute each hook if a previous hook throws an exception.</p>
 *
 * <p>If the targeted method throws an exception internally, that exception is wrapped inside a {@link TargetInvocationException}
 * and can be retrieved by {@code getCause()}.</p>
 *
 * <p>The target method or constructor is invoked on a separate thread of a shared {@link TimeoutRunner}.
 * If it takes more time than the pre-configured timeout duration, an {@link InvocationTimeoutException} is thrown
 * and the thread is interrupted.
 * This timeout can be configured via {@link plus.einfprog.Settings} during library initialization.</p>
 *
 * @see Pipeline
 * @see BeforeHook
 * @see AfterHook
 * @see ExceptionHook
 * @see TargetInvocationException
 * @see InvocationTimeoutException
 */
public interface InvocationPipeline {

    /**
     * Initializes the before, after, and exception pipelines without hooks.
     *
     * @return An {@link InvocationPipeline} object with empty pipelines.
     */
    static InvocationPipeline empty() {
        return new DefaultInvocationPipeline(
                new Pipeline<>(beforeHook -> beforeHook::apply),
                new Pipeline<>(afterHook -> afterHook::apply),
                new Pipeline<>(exceptionHook -> exceptionHook::apply));
    }

    /**
     * Unwraps proxies in the request and resolves it, runs the before hooks, invokes the resolved executable
     * with the configured timeout, runs the after hooks, and wraps the return value into a proxy if the
     * requested return type is one.
     *
     * @param request the requested invocation, possibly containing proxies
     * @return the result after all after hooks have been applied
     * @throws plus.einfprog.exception.TargetNotFoundException if no matching method or constructor exists
     * @throws TargetInvocationException                       if the target method or constructor threw an exception
     * @throws InvocationTimeoutException                      if the invocation didn't finish within the configured timeout
     */
    InvocationResult run(InvocationRequest request);

    /**
     * @return the pipeline executed before the target method is invoked
     */
    Pipeline<Invocation, BeforeHook> before();

    /**
     * @return the pipeline executed after the target method has been invoked if and only if there was no internal exception
     */
    Pipeline<InvocationResult, AfterHook> after();

    /**
     * @return the pipeline executed if and only if an exception occurs during method invocation
     */
    Pipeline<Throwable, ExceptionHook> exception();
}
