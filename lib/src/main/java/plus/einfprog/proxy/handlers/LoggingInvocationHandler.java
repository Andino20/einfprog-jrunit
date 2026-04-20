package plus.einfprog.proxy.handlers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingInvocationHandler implements InvocationHandler {

    private final Object subject;
    private final InvocationHandler next;

    public LoggingInvocationHandler(Object subject, InvocationHandler next) {
        this.subject = subject;
        this.next = next;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.printf("Called %s on object %s with arguments: %s\n", method.getName(), subject, Arrays.toString(args));
        Object returnValue = next.invoke(proxy, method, args);
        System.out.printf("\tReturned: %s\n", returnValue);
        return returnValue;
    }

}
