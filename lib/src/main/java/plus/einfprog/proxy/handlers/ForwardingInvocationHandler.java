package plus.einfprog.proxy.handlers;

import java.lang.reflect.Method;

public class ForwardingInvocationHandler implements SubjectInvocationHandler {

    private final Object subject;

    public ForwardingInvocationHandler(Object subject) {
        this.subject = subject;
    }

    @Override
    public Object getSubject() {
        return subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(subject, args);
    }

}
