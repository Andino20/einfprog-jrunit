package plus.einfprog.proxy;

import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class ProxyDelegate implements TargetInvocationHandler {

    private final Object target;
    private final InvocationPipeline pipeline;

    public ProxyDelegate(Object target, InvocationPipeline pipeline) {
        this.target = target;
        this.pipeline = pipeline;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        MethodCallResult result = pipeline.run(MethodCall.from(method)
                .withArguments(List.of(Objects.requireNonNullElse(args, new Object[0])))
                .withTargetClass(target.getClass())
                .withTarget(target));
        return result.returnValue();
    }

    @Override
    public Object getTarget() {
        return target;
    }

}
