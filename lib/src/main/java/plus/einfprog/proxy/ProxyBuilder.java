package plus.einfprog.proxy;

import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

public class ProxyBuilder<T> {

    private final Class<T> proxyClass;
    private final Object subject;
    private final InvocationPipeline pipeline = InvocationPipeline.empty();

    public ProxyBuilder(Class<T> proxyClass, Object subject) {
        this.proxyClass = proxyClass;
        this.subject = subject;
    }

    public ProxyBuilder<T> autoWrapping() {
        ProxyAutoWrapper autoWrapper = new ProxyAutoWrapper();
        pipeline.before().addFirst(autoWrapper);
        pipeline.after().addLast(autoWrapper);
        return this;
    }

    public T build() {
        ProxyDelegate handler = new ProxyDelegate(subject, pipeline);
        return proxyClass.cast(java.lang.reflect.Proxy.newProxyInstance(
                proxyClass.getClassLoader(), new Class<?>[]{proxyClass}, handler));
    }

}
