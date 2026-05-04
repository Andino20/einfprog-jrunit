package plus.einfprog.pipeline;

import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.intercepter.AfterInterceptor;
import plus.einfprog.pipeline.intercepter.BeforeInterceptor;
import plus.einfprog.pipeline.intercepter.ExceptionInterceptor;

public record InvocationPipeline(Pipeline<MethodCall, BeforeInterceptor> before,
                                 Pipeline<MethodCallResult, AfterInterceptor> after,
                                 Pipeline<Throwable, ExceptionInterceptor> exception) {

    public static InvocationPipeline empty() {
        return new InvocationPipeline(
                new Pipeline<>(beforeInterceptor -> beforeInterceptor::intercept),
                new Pipeline<>(afterInterceptor -> afterInterceptor::intercept),
                new Pipeline<>(exceptionInterceptor -> exceptionInterceptor::intercept));
    }

}
