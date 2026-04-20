package plus.einfprog.proxy;

import java.util.Objects;

import static plus.einfprog.Util.types;

public interface ProxyUtil {

    static <T> T create(Class<T> proxyClass, Object... args) {
        String name = Objects.requireNonNull(proxyClass.getDeclaredAnnotation(Proxy.class)).value();
        Class<?>[] types = types(args);
        try {
            Class<?> subjectClass = Class.forName(name);
            Object subject = subjectClass.getConstructor(types).newInstance(args);
            return new ProxyBuilder<>(proxyClass, subject)
                    .addLogger()
                    .build();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
