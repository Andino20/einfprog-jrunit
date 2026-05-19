package plus.einfprog.proxy;

import plus.einfprog.ReflectiveException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Objects;

import static org.joor.Reflect.*;

public interface ProxyHelper {

    static <T> T create(Class<T> proxyClass, Object... args) {
        String name = Objects.requireNonNull(proxyClass.getDeclaredAnnotation(Proxy.class)).value();
        Object[] arguments = Arrays.stream(args).map(ProxyHelper::unwrap).toArray(Object[]::new);

        Object subject = args.length > 0 ? onClass(name).create(arguments).get() : onClass(name).create().get();
        return wrap(subject, proxyClass);
    }

    static <T> T wrap(Object subject, Class<T> proxyClass) {
        if (subject == null) return null; // TODO: can proxies be null?
        if (!canWrap(subject, proxyClass))
            throw new IllegalArgumentException(String.format("Subject %s can not be wrapped in class %s", subject, proxyClass.getSimpleName()));
        // return on(subject).as(proxyClass); // TODO: this is what I want
        return new ProxyBuilder<>(proxyClass, subject).build();
    }

    static Object unwrap(Object proxy) {
        if (proxy == null) return null;
        boolean isProxy = java.lang.reflect.Proxy.isProxyClass(proxy.getClass());
        if (isProxy) {
            InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(proxy);
            if (handler instanceof TargetInvocationHandler subjectHandler) {
                return subjectHandler.getTarget();
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

    static boolean isProxyClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Proxy.class);
    }

    static Class<?> getTargetClass(Class<?> proxyClass) throws ReflectiveException {
        try {
            Proxy annotation = proxyClass.getDeclaredAnnotation(Proxy.class);
            if (Objects.nonNull(annotation)) {
                return Class.forName(annotation.value());
            } else throw new IllegalArgumentException("Class is not a proxy.");
        } catch (ClassNotFoundException e) {
            throw new ReflectiveException(e);
        }
    }

    static Object deepUnwrapProxyArray(Object source) {
        if (source == null) return null;

        Class<?> sourceClass = source.getClass();
        if (!sourceClass.isArray())
            throw new IllegalArgumentException("Argument must be an array.");

        Class<?> componentClass = sourceClass.getComponentType();
        if (componentClass.isArray()) {
            Class<?> deepComponentClass = componentClass;
            int dim = 1;
            while (deepComponentClass.isArray()) {
                deepComponentClass = deepComponentClass.getComponentType();
                dim++;
            }

            Class<?> arrayType = getTargetClass(deepComponentClass);
            for (int i = 0; i < dim - 1; i++) {
                arrayType = arrayType.arrayType();
            }

            Object unwrapped = Array.newInstance(arrayType, Array.getLength(source));
            for (int i = 0; i < Array.getLength(source); i++) {
                Array.set(unwrapped, i, deepUnwrapProxyArray(Array.get(source, i)));
            }
            return unwrapped;
        }

        Class<?> targetClass = getTargetClass(componentClass);
        int length = Array.getLength(source);
        Object unwrapped = Array.newInstance(targetClass, length);
        for (int i = 0; i < length; i++) {
            Array.set(unwrapped, i, unwrap(Array.get(source, i)));
        }
        return unwrapped;
    }

    static Object deepWrapAsProxyArray(Object source, Class<?> proxyClass) {
        if (source == null) return null;
        if (!isProxyClass(proxyClass))
            throw new IllegalArgumentException("Class is not a proxy.");

        Class<?> sourceClass = source.getClass();
        if (!sourceClass.isArray())
            throw new IllegalArgumentException("Argument must be an array.");

        Class<?> componentClass = sourceClass.getComponentType();
        if (componentClass.isArray()) {
            int dim = 1;
            Class<?> tmp = componentClass;
            while (tmp.isArray()) {
                tmp = tmp.getComponentType();
                dim++;
            }

            Class<?> arrayType = proxyClass;
            for (int i = 0; i < dim - 1; i++) {
                arrayType = arrayType.arrayType();
            }

            Object arr = Array.newInstance(arrayType, Array.getLength(source));
            for (int i = 0; i < Array.getLength(source); i++) {
                Array.set(arr, i, deepWrapAsProxyArray(Array.get(source, i), proxyClass));
            }
            return arr;
        }

        int length = Array.getLength(source);
        Object arr = Array.newInstance(proxyClass, length);
        for (int i = 0; i < length; i++) {
            Array.set(arr, i, wrap(Array.get(source, i), proxyClass));
        }
        return arr;
    }

}
