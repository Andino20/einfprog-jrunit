package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.InvocationResult;

@FunctionalInterface
public interface AfterHook {

    InvocationResult intercept(InvocationResult result);

}
