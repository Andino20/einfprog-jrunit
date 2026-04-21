package plus.einfprog.proxy.handlers;

import java.lang.reflect.InvocationHandler;

public interface SubjectInvocationHandler extends InvocationHandler {
    Object getSubject();
}
