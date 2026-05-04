package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.MethodCall;

@FunctionalInterface
public interface BeforeInterceptor {

    MethodCall intercept(MethodCall call);

}
