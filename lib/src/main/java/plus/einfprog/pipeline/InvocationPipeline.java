package plus.einfprog.pipeline;

import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.intercepter.AfterInterceptor;
import plus.einfprog.pipeline.intercepter.BeforeInterceptor;
import plus.einfprog.pipeline.intercepter.ExceptionInterceptor;
import plus.einfprog.util.PrependedList;

import java.util.Objects;

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
        try {
            call = before().run(call);
            Object returnValue = Objects.requireNonNull(call.methodHandle())
                    .invokeWithArguments(call.isStatic() ? call.arguments() : new PrependedList<>(call.target(), call.arguments()));
            return after().run(MethodCallResult.from(call, returnValue));
        } catch (Throwable e) {
            Throwable t = exception().run(e);
            throw new RuntimeException("Failed to invoke method: " + t.getMessage());
        }
    }

}
