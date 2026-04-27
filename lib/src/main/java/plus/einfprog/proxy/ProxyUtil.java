package plus.einfprog.proxy;

import plus.einfprog.proxy.handlers.SubjectInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Objects;

import static org.joor.Reflect.*;

public interface ProxyUtil {

    static <T> T create(Class<T> proxyClass, Object... args) {
        String name = Objects.requireNonNull(proxyClass.getDeclaredAnnotation(Proxy.class)).value();
        Object[] arguments = Arrays.stream(args).map(ProxyUtil::unwrap).toArray(Object[]::new);

        Object subject = args.length > 0 ? onClass(name).create(arguments).get() : onClass(name).create().get();
        return wrap(subject, proxyClass);
    }

    static <T> T wrap(Object subject, Class<T> proxyClass) {
        if (subject == null) return null; // TODO: can proxies be null?

        if (!canWrap(subject, proxyClass))
            throw new IllegalArgumentException(String.format("Subject %s can not be wrapped in class %s", subject, proxyClass.getSimpleName()));
        return new ProxyBuilder<>(proxyClass, subject)
                .autoWrapping()
                .build();
    }

    static Object unwrap(Object proxy) {
        if (proxy == null) return null;
        boolean isProxy = java.lang.reflect.Proxy.isProxyClass(proxy.getClass());
        if (isProxy) {
            InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(proxy);
            if (handler instanceof SubjectInvocationHandler subjectHandler) {
                return subjectHandler.getSubject();
            }
        }
        return proxy;
    }

    static boolean canWrap(Object subject, Class<?> proxyClass) {
        Proxy annotation = proxyClass.getDeclaredAnnotation(Proxy.class);
        if (annotation == null)
            return false;

        try {
            Class<?> subjectClass = Class.forName(annotation.value());
            return subjectClass.isInstance(subject);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
