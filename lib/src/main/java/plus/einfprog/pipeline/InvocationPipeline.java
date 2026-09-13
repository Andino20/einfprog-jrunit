package plus.einfprog.pipeline;

import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.intercepter.AfterInterceptor;
import plus.einfprog.pipeline.intercepter.BeforeInterceptor;
import plus.einfprog.pipeline.intercepter.ExceptionInterceptor;
import plus.einfprog.util.PrependedList;

import java.lang.invoke.WrongMethodTypeException;
import java.util.concurrent.*;

public record InvocationPipeline(Pipeline<MethodCall, BeforeInterceptor> before,
                                 Pipeline<MethodCallResult, AfterInterceptor> after,
                                 Pipeline<Throwable, ExceptionInterceptor> exception) {

    public static InvocationPipeline empty() {
        return new InvocationPipeline(
                new Pipeline<>(beforeInterceptor -> beforeInterceptor::intercept),
                new Pipeline<>(afterInterceptor -> afterInterceptor::intercept),
                new Pipeline<>(exceptionInterceptor -> exceptionInterceptor::intercept));
    }

    public MethodCallResult run(MethodCall call) {
        call = before().run(call);
        Object returnValue = invokeWithTimeout(call, 1);
        return after().run(MethodCallResult.from(call, returnValue));
    }

    private Object invokeWithTimeout(MethodCall call, long seconds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Object> returnValue = executor.submit(() -> invoke(call));

        try {
            return returnValue.get(seconds, TimeUnit.SECONDS);
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
