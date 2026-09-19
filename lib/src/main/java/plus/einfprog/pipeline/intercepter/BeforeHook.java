package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.MethodCall;

@FunctionalInterface
public interface BeforeHook {

    MethodCall intercept(MethodCall call);

}
