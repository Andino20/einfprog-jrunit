package plus.einfprog.proxy;

import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.MethodCall;
import plus.einfprog.pipeline.MethodCallResult;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyDelegate implements InvocationHandler {

    private final Object target;
    private final InvocationPipeline pipeline;

    public ProxyDelegate(Object target, InvocationPipeline pipeline) {
        this.target = target;
        this.pipeline = pipeline;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        MethodCallResult result = null;
        try {
            MethodCall call = pipeline.before().run(new MethodCall(target, method, args));
            result = new MethodCallResult(call.method().invoke(call.target(), call.args()));
        } catch (Throwable t) {
            throw pipeline.exception().run(t);
        } finally {
            pipeline.after().run(result);
        }
        return result.returnValue();
    }

}
