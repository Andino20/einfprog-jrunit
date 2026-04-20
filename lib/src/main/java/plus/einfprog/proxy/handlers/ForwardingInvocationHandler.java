package plus.einfprog.proxy.handlers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static plus.einfprog.Util.types;

public class ForwardingInvocationHandler implements InvocationHandler {

    private final Object subject;

    public ForwardingInvocationHandler(Object subject) {
        this.subject = subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        Class<?>[] types = types(args);
        return subject.getClass().getMethod(name, types).invoke(subject, args);
    }

}
