package plus.einfprog.proxy.handlers;

import plus.einfprog.proxy.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

public class AutoWrappingHandler implements InvocationHandler {

    private InvocationHandler next;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

    private Class<?> unwrap(Class<?> clazz) throws ClassNotFoundException {
        Proxy pa = clazz.getDeclaredAnnotation(Proxy.class);
        return Objects.nonNull(pa) ?
                Class.forName(pa.value()) :
                clazz;
    }

}
