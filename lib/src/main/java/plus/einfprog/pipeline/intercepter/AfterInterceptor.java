package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.MethodCallResult;

@FunctionalInterface
public interface AfterInterceptor {

    MethodCallResult intercept(MethodCallResult result);

}
