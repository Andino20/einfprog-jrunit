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
 * The implementation of {@link InvocationPipeline}, created through {@link InvocationPipeline#empty()}.
 *
 * @param before    the pipeline executed before the target method is invoked
 * @param after     the pipeline executed after the target method has been invoked without an exception
 * @param exception the pipeline executed if the target method throws an exception
 */
record DefaultInvocationPipeline(Pipeline<Invocation, BeforeHook> before,
                                 Pipeline<InvocationResult, AfterHook> after,
                                 Pipeline<Throwable, ExceptionHook> exception) implements InvocationPipeline {

    private static final TimeoutRunner TIMEOUT_RUNNER = new TimeoutRunner("einfprog-invocation");

    @Override
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
