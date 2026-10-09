package plus.einfprog.pipeline.hook;

import plus.einfprog.pipeline.InvocationResult;

@FunctionalInterface
public interface AfterHook {

    InvocationResult apply(InvocationResult result);

}
