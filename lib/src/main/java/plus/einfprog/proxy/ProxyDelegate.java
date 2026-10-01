package plus.einfprog.proxy;

import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.dto.Invocation;
import plus.einfprog.pipeline.dto.InvocationResult;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Implements a subtype of {@link java.lang.reflect.InvocationHandler} and is therefore
 * responsible for handling method invocations on proxy interfaces and forwarding
 * those calls to a target object.
 * This is done by invoking the {@link InvocationPipeline}
 * with the information and arguments provided by the method call.
 */
public class ProxyDelegate implements TargetInvocationHandler {

    private final Object target;
    private final InvocationPipeline pipeline;

    public ProxyDelegate(Object target, InvocationPipeline pipeline) {
        this.target = target;
        this.pipeline = pipeline;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) {
        InvocationResult result = pipeline.run(Invocation.from(method)
                .withArguments(Arrays.asList(Objects.requireNonNullElse(args, new Object[0])))
                .withTargetClass(target.getClass())
                .withTarget(target));
        return result.returnValue();
    }

    @Override
    public Object getTarget() {
        return target;
    }

}
