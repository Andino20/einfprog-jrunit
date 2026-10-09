package plus.einfprog.proxy;

import plus.einfprog.exception.TargetNotFoundException;
import plus.einfprog.reflection.Reflected;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.util.*;

/**
 * Utility class for working with proxies.
 */
public interface ProxyUtil {

    static <T> T create(Class<T> proxyClass, Object... args) {
        String targetClassName = Objects.requireNonNull(proxyClass.getDeclaredAnnotation(Proxy.class)).value();
        Object target = Reflected.on(targetClassName).create(args).get();
        return wrap(target, proxyClass);
    }

    /**
     * Wraps the given target in a proxy of the specified class.
     *
     * <p>If {@code target} is {@code null}, {@code null} is returned.
     *
     * @param target     the object to wrap; may be {@code null}
     * @param proxyClass the proxy class to wrap the target in; must not be {@code null}
     * @return a proxy of type {@code T} wrapping the target, or {@code null} if target is {@code null}
     * @throws NullPointerException     if {@code proxyClass} is {@code null}
     * @throws IllegalArgumentException if the target cannot be wrapped in the given proxy class
     */
    static <T> T wrap(Object target, Class<T> proxyClass) {
        Objects.requireNonNull(proxyClass, "Proxy class must not be null.");
        if (target == null)
            return null;

        if (!canWrap(target, proxyClass))
            throw new IllegalArgumentException(String.format("Target of type %s can not be wrapped in %s",
                    target.getClass().getSimpleName(),
                    proxyClass.getSimpleName()));

        return new ProxyBuilder<>(proxyClass, target).build();
    }

    /**
     * Wraps the elements of an array in the specified proxy class, supporting
     * both single-dimensional and multidimensional arrays.
     *
     * <p>For multidimensional arrays, a new array of equivalent dimensions is
     * returned with each element recursively wrapped.
     * For single-dimensional arrays, each element is wrapped via {@link #wrap(Object, Class)}.
     *
     * <p>If {@code source} is {@code null}, {@code null} is returned.
     *
     * @param source     the array to wrap; may be {@code null}
     * @param proxyClass the proxy class to wrap each element in
     * @return a new array of type {@code proxyClass} with each element wrapped, or
     * {@code null} if {@code source} is {@code null}
     * @throws NullPointerException     if {@code proxyClass} is {@code null}
     * @throws IllegalArgumentException if {@code proxyClass} is not a proxy class,
     *                                  if {@code source} is not an array, or if the
     *                                  array's component type cannot be wrapped in {@code proxyClass}
     */
    static Object wrapArray(Object source, Class<?> proxyClass) {
        Objects.requireNonNull(proxyClass, "Proxy class must not be null.");
        if (source == null)
            return null;
        if (!isProxyClass(proxyClass))
            throw new IllegalArgumentException("Class is not a proxy.");

        Class<?> sourceClass = source.getClass();
        if (!sourceClass.isArray())
            throw new IllegalArgumentException("Argument must be an array.");

        Class<?> componentClass = sourceClass.getComponentType();
        if (componentClass.isArray()) {
            int dim = 1;
            Class<?> deepComponentClass = componentClass;
            while (deepComponentClass.isArray()) {
                deepComponentClass = deepComponentClass.getComponentType();
                dim++;
            }

            if (!canWrap(deepComponentClass, proxyClass))
                throw new IllegalArgumentException("Component class of array can not be wrapped in proxy.");

            Class<?> arrayType = proxyClass;
            for (int i = 0; i < dim - 1; i++) {
                arrayType = arrayType.arrayType();
            }

            Object arr = Array.newInstance(arrayType, Array.getLength(source));
            for (int i = 0; i < Array.getLength(source); i++) {
                Array.set(arr, i, wrapArray(Array.get(source, i), proxyClass));
            }
            return arr;
        }

        if (!canWrap(componentClass, proxyClass))
            throw new IllegalArgumentException("Component class of array can not be wrapped in proxy.");

        int length = Array.getLength(source);
        Object arr = Array.newInstance(proxyClass, length);
        for (int i = 0; i < length; i++) {
            Array.set(arr, i, wrap(Array.get(source, i), proxyClass));
        }
        return arr;
    }

    /**
     * Unwraps a proxy instance, returning the underlying target object.
     * If {@code proxy} is not a proxy or null, it is returned as-is.
     * If {@code proxy} is an array, the unwrapping is delegated to {@link #unwrapArray(Object)}.
     *
     * @param proxy the object to unwrap; may be null
     * @return the unwrapped target, or the original argument if not a proxy
     * @see #unwrapArray(Object)
     */
    static Object unwrap(Object proxy) {
        if (proxy == null)
            return null;
        if (proxy.getClass().isArray())
            return unwrapArray(proxy);

        boolean isProxy = java.lang.reflect.Proxy.isProxyClass(proxy.getClass());
        if (isProxy) {
            InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(proxy);
            if (handler instanceof TargetInvocationHandler targetHandler) {
                return targetHandler.getTarget();
            }
        }
        return proxy;
    }

    /**
     * Unwraps a proxy array to an array of the underlying target type, supporting
     * both single-dimensional and multidimensional arrays.
     *
     * <p>If the array's component type is not a proxy, the original array is
     * returned as-is. For multidimensional arrays, the component type of the
     * deepest dimension is inspected; if it is a proxy type, a new array of
     * equivalent dimensions is returned with each element recursively unwrapped.
     *
     * <p>If {@code source} is {@code null}, {@code null} is returned.
     *
     * @param source the array to unwrap; may be {@code null}
     * @return a new array of the unwrapped target type, the original array if no
     * unwrapping was necessary, or {@code null} if {@code source} is {@code null}
     * @throws IllegalArgumentException if {@code source} is not an array
     */
    static Object unwrapArray(Object source) {
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

            if (!isProxyClass(deepComponentClass))
                return source;

            Class<?> arrayType = getTargetClass(deepComponentClass);
            for (int i = 0; i < dim - 1; i++) {
                arrayType = arrayType.arrayType();
            }

            Object unwrapped = Array.newInstance(arrayType, Array.getLength(source));
            for (int i = 0; i < Array.getLength(source); i++) {
                Array.set(unwrapped, i, unwrapArray(Array.get(source, i)));
            }
            return unwrapped;
        }

        if (!isProxyClass(componentClass))
            return source;

        Class<?> targetClass = getTargetClass(componentClass);
        int length = Array.getLength(source);
        Object unwrapped = Array.newInstance(targetClass, length);
        for (int i = 0; i < length; i++) {
            Array.set(unwrapped, i, unwrap(Array.get(source, i)));
        }
        return unwrapped;
    }

    /**
     * Unwraps a proxy class to its underlying target class.
     *
     * <p>If the given class is an array type, the unwrapping is delegated to
     * {@link #unwrapArrayClass(Class)}. Otherwise, if the class is a proxy,
     * its target class is returned. If the class is neither a proxy nor an
     * array, it is returned as-is.
     *
     * @param proxyClass the class to unwrap; must not be {@code null}
     * @return the unwrapped target class, or {@code proxyClass} itself if it is not a proxy
     * @throws NullPointerException if {@code proxyClass} is {@code null}
     * @see #unwrapArrayClass(Class)
     */
    static Class<?> unwrapClass(Class<?> proxyClass) {
        Objects.requireNonNull(proxyClass, "Proxy class must not be null.");
        if (proxyClass.isArray())
            return unwrapArrayClass(proxyClass);

        return ProxyUtil.isProxyClass(proxyClass) ?
                ProxyUtil.getTargetClass(proxyClass) :
                proxyClass;
    }

    /**
     * Unwraps a proxy array class to its underlying target array class.
     *
     * <p>The dimensionality of the array is preserved: if the given class is a
     * two-dimensional proxy array, the returned class will be a two-dimensional
     * array of the proxy's target type. If the component type is not a proxy,
     * the class is returned as-is.
     *
     * @param proxyArrayClass the array class to unwrap; must not be {@code null}
     * @return the unwrapped target array class, or {@code proxyArrayClass} itself if its component type is not a proxy
     * @throws NullPointerException     if {@code proxyArrayClass} is {@code null}
     * @throws IllegalArgumentException if {@code proxyArrayClass} is not an array class
     * @see #unwrapClass(Class)
     */
    static Class<?> unwrapArrayClass(Class<?> proxyArrayClass) {
        Objects.requireNonNull(proxyArrayClass, "Proxy array class must not be null.");
        if (!proxyArrayClass.isArray())
            throw new IllegalArgumentException("Proxy array class must be an array.");

        int dim = 0;
        Class<?> type = proxyArrayClass;
        while (type.isArray()) {
            type = type.getComponentType();
            dim++;
        }

        if (ProxyUtil.isProxyClass(type)) {
            Class<?> targetClass = ProxyUtil.getTargetClass(type);
            for (int i = 0; i < dim; i++) {
                targetClass = targetClass.arrayType();
            }
            return targetClass;
        }
        return proxyArrayClass;
    }

    /**
     * Returns whether the given target can be wrapped in the specified proxy class.
     *
     * <p>Returns {@code false} if {@code proxyClass} is not a proxy class, if the
     * target's type is not compatible with the proxy's target class, or if any
     * non-reflective exception occurs during resolution. A {@code null} target
     * is considered wrappable in any valid proxy class.
     *
     * @param target     the object to wrap; may be {@code null}
     * @param proxyClass the proxy class to wrap the target in; may be {@code null}
     * @return {@code true} if the target can be wrapped in the given proxy class
     * @throws TargetNotFoundException if the proxy's target class could not be resolved
     */
    static boolean canWrap(Object target, Class<?> proxyClass) {
        if (!isProxyClass(proxyClass))
            return false;

        try {
            Class<?> targetClass = getTargetClass(proxyClass);
            return target == null || targetClass.isInstance(target);
        } catch (TargetNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns whether instances of the given target class can be wrapped in the
     * specified proxy class.
     *
     * <p>Returns {@code false} if either argument is {@code null}, if {@code proxyClass}
     * is not a proxy class, if {@code targetType} is not assignable to the proxy's
     * target class, or if any non-reflective exception occurs during resolution.
     *
     * @param targetType the class to check assignability for; may be {@code null}
     * @param proxyClass the proxy class to wrap instances in; may be {@code null}
     * @return {@code true} if instances of {@code targetType} can be wrapped in the given proxy class
     * @throws TargetNotFoundException if the proxy's target class could not be resolved
     */
    static boolean canWrap(Class<?> targetType, Class<?> proxyClass) {
        if (targetType == null || !isProxyClass(proxyClass))
            return false;

        try {
            Class<?> targetClass = getTargetClass(proxyClass);
            return targetClass.isAssignableFrom(targetType);
        } catch (TargetNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns whether the given class is a proxy class.
     *
     * <p>A class is considered a proxy if it is directly annotated with {@link Proxy},
     * or if any of its implemented interfaces are annotated with {@link Proxy}.
     * Returns {@code false} if {@code clazz} is {@code null}.
     *
     * @param clazz the class to check; may be {@code null}
     * @return {@code true} if the class is a proxy class
     */
    static boolean isProxyClass(Class<?> clazz) {
        return clazz != null && (clazz.isAnnotationPresent(Proxy.class) || Arrays.stream(clazz.getInterfaces()).anyMatch(x -> x.isAnnotationPresent(Proxy.class)));
    }

    /**
     * Resolves the target class of the given proxy class.
     *
     * <p>The target class is determined by the {@link Proxy} annotation, inspecting
     * first the class itself, then its implemented interfaces. The annotation's value
     * is used to resolve the target class by name.
     *
     * @param proxyClass the proxy class to resolve the target class for; must not be {@code null}
     * @return the resolved target class
     * @throws IllegalArgumentException if {@code proxyClass} is not a proxy class
     * @throws TargetNotFoundException  if the target class could not be found on the classpath
     */
    static Class<?> getTargetClass(Class<?> proxyClass) throws TargetNotFoundException {
        try {
            Proxy annotation = proxyClass.getDeclaredAnnotation(Proxy.class);
            if (annotation != null) {
                return Class.forName(annotation.value());
            } else {
                Optional<Proxy> optAnnotation = Arrays.stream(proxyClass.getInterfaces())
                        .map(i -> i.getAnnotation(Proxy.class))
                        .filter(Objects::nonNull)
                        .findAny();
                if (optAnnotation.isPresent()) {
                    return Class.forName(optAnnotation.get().value());
                }
                throw new IllegalArgumentException("Class is not a proxy.");
            }
        } catch (ClassNotFoundException e) {
            throw new TargetNotFoundException(e);
        }
    }

}
