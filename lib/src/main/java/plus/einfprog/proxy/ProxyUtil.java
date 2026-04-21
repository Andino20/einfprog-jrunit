package plus.einfprog.proxy;

import java.util.Objects;

import static org.joor.Reflect.*;

public interface ProxyUtil {

    static <T> T create(Class<T> proxyClass, Object... args) {
        String name = Objects.requireNonNull(proxyClass.getDeclaredAnnotation(Proxy.class)).value();
        Object subject = args.length > 0 ? onClass(name).create(args).get() : onClass(name).create().get();
        return wrap(subject, proxyClass);
    }

    static <T> T wrap(Object subject, Class<T> proxyClass) {
        if (!canWrap(subject, proxyClass))
            throw new IllegalArgumentException(String.format("Subject %s can not be wrapped in class %s", subject, proxyClass.getSimpleName()));
        return new ProxyBuilder<>(proxyClass, subject)
                .autoWrapping()
                .build();
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
