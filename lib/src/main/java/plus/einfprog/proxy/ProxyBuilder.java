package plus.einfprog.proxy;

import plus.einfprog.EinfprogJRunit;

public class ProxyBuilder<T> {

    private final Class<T> proxyClass;
    private final Object subject;

    public ProxyBuilder(Class<T> proxyClass, Object subject) {
        this.proxyClass = proxyClass;
        this.subject = subject;
    }

    public T build() {
        ProxyDelegate handler = new ProxyDelegate(subject, EinfprogJRunit.getContext().pipeline());
        return proxyClass.cast(java.lang.reflect.Proxy.newProxyInstance(
                proxyClass.getClassLoader(), new Class<?>[]{proxyClass}, handler));
    }

}
