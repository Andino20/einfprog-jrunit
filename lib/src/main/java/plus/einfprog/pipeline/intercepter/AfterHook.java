package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.MethodCallResult;

@FunctionalInterface
public interface AfterHook {

    MethodCallResult intercept(MethodCallResult result);

}
