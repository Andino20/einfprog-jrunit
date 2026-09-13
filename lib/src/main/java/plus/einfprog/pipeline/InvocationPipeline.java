package plus.einfprog.pipeline;

import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.intercepter.AfterInterceptor;
import plus.einfprog.pipeline.intercepter.BeforeInterceptor;
import plus.einfprog.pipeline.intercepter.ExceptionInterceptor;
import plus.einfprog.util.PrependedList;

import java.lang.invoke.WrongMethodTypeException;

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
        Object returnValue = invoke(call);
        return after().run(MethodCallResult.from(call, returnValue));
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
