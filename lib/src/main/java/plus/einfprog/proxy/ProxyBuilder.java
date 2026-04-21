package plus.einfprog.proxy;

import plus.einfprog.proxy.handlers.AutoWrappingHandler;
import plus.einfprog.proxy.handlers.ForwardingInvocationHandler;
import plus.einfprog.proxy.handlers.SubjectInvocationHandler;

public class ProxyBuilder<T> {

    private final Class<T> proxyClass;
    private final Object subject;
    private SubjectInvocationHandler handler;

    public ProxyBuilder(Class<T> proxyClass, Object subject) {
        this.proxyClass = proxyClass;
        this.subject = subject;
        handler = new ForwardingInvocationHandler(subject);
    }

    public ProxyBuilder<T> autoWrapping() {
        handler = new AutoWrappingHandler(subject, handler);
        return this;
    }

    public T build() {
        return proxyClass.cast(java.lang.reflect.Proxy.newProxyInstance(
                proxyClass.getClassLoader(), new Class<?>[]{proxyClass}, handler));
    }

}
