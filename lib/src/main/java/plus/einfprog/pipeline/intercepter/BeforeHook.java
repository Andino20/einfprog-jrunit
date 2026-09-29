package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.Invocation;

@FunctionalInterface
public interface BeforeHook {

    Invocation intercept(Invocation invocation);

}
