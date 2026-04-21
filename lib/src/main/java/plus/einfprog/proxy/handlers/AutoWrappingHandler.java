package plus.einfprog.proxy.handlers;

import plus.einfprog.proxy.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static plus.einfprog.proxy.ProxyUtil.wrap;

public class AutoWrappingHandler implements SubjectInvocationHandler {

    private final Object subject;
    private SubjectInvocationHandler next;

    public AutoWrappingHandler(Object subject, SubjectInvocationHandler next) {
        this.subject = subject;
        this.next = next;
    }

    @Override
    public Object getSubject() {
        return next.getSubject();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        Class<?>[] types = unwrapProxyClasses(method.getParameterTypes());
        Object[] arguments = unwrapProxyInstances(args);

        Method m = subject.getClass().getMethod(name, types);
        Object returnValue = next.invoke(proxy, m, arguments);

        Class<?> returnType = method.getReturnType();
        return returnType.isAnnotationPresent(Proxy.class) ?
                wrap(returnValue, returnType) :
                returnValue;
    }

    private Class<?>[] unwrapProxyClasses(Class<?>[] classes) {
        return Arrays.stream(classes)
                .map(this::unwrapProxyClass)
                .toArray(Class<?>[]::new);
    }

    private Class<?> unwrapProxyClass(Class<?> clazz) {
        try {
            Proxy pa = clazz.getDeclaredAnnotation(Proxy.class);
            return Objects.nonNull(pa) ?
                    Class.forName(pa.value()) :
                    clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] unwrapProxyInstances(Object[] os) {
        return Objects.nonNull(os)
                ? Arrays.stream(os).map(this::unwrapProxyInstance).toArray()
                : null;
    }

    private Object unwrapProxyInstance(Object o) {
        if (o == null) return null;
        boolean isProxy = java.lang.reflect.Proxy.isProxyClass(o.getClass());
        if (isProxy) {
            InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(o);
            if (handler instanceof SubjectInvocationHandler subjectHandler) {
                return subjectHandler.getSubject();
            }
        }
        return o;
    }

}
