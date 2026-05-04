package plus.einfprog.proxy;

import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ProxyDelegate implements TargetInvocationHandler {

    private final Object target;
    private final InvocationPipeline pipeline;

    public ProxyDelegate(Object target, InvocationPipeline pipeline) {
        this.target = target;
        this.pipeline = pipeline;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        try {
            UUID traceId = UUID.randomUUID();
            MethodCall call = pipeline.before().run(new MethodCall(traceId, target, method, args));
            Object returnValue = invoke(call);
            MethodCallResult result = pipeline.after().run(new MethodCallResult(traceId, call, returnValue));

            return result.returnValue();
        } catch (Throwable t) {
            throw pipeline.exception().run(t);
        }
    }

    public Object invoke(MethodCall call) throws IllegalAccessException, InvocationTargetException {
        return call.method().invoke(target, call.args());
    }

    @Override
    public Object getTarget() {
        return target;
    }

}
