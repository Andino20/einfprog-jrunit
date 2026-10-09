package plus.einfprog.proxy;

import plus.einfprog.EinfprogJRunit;

public class ProxyBuilder<T> {

    private final Class<T> proxyClass;
    private final Object target;

    public ProxyBuilder(Class<T> proxyClass, Object target) {
        this.proxyClass = proxyClass;
        this.target = target;
    }

    public T build() {
        ProxyDelegate handler = new ProxyDelegate(target, EinfprogJRunit.getContext().pipeline());
        return proxyClass.cast(java.lang.reflect.Proxy.newProxyInstance(
                proxyClass.getClassLoader(), new Class<?>[]{proxyClass}, handler));
    }

}
