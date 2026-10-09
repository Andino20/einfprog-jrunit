package plus.einfprog.pipeline.hook;

import plus.einfprog.pipeline.Invocation;

@FunctionalInterface
public interface BeforeHook {

    Invocation apply(Invocation invocation);

}
