package plus.einfprog.proxy;

import plus.einfprog.proxy.handlers.ForwardingInvocationHandler;
import plus.einfprog.proxy.handlers.LoggingInvocationHandler;

import java.lang.reflect.InvocationHandler;

public class ProxyBuilder<T> {

    private final Class<T> proxyClass;
    private final Object subject;
    private InvocationHandler handler;

    public ProxyBuilder(Class<T> proxyClass, Object subject) {
        this.proxyClass = proxyClass;
        this.subject = subject;
        handler = new ForwardingInvocationHandler(subject);
    }

    public ProxyBuilder<T> autoWrapping() {
        return this;
    }

    public ProxyBuilder<T> addLogger() {
        handler = new LoggingInvocationHandler(subject, handler);
        return this;
    }

    public T build() {
        return proxyClass.cast(java.lang.reflect.Proxy.newProxyInstance(
                proxyClass.getClassLoader(), new Class<?>[]{proxyClass}, handler));
    }

}
