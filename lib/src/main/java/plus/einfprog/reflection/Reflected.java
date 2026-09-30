package plus.einfprog.reflection;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.pipeline.RuntimeReflectiveOperationException;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.dto.Invocation;
import plus.einfprog.proxy.ProxyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Fluent reflection wrapper for executing methods dynamically through the framework's invocation pipeline.
 * <p>
 * This class abstracts reflective method execution on both static classes and object instances,
 * automatically routing calls through the {@link InvocationPipeline} for interception, proxying, and tracing.
 * Return values are wrapped in a new {@code Reflected} instance, enabling fluent method chaining.
 * </p>
 *
 * @see #on(String)
 * @see #on(Object)
 */
public class Reflected {

    private final Class<?> type;
    private final Object target;
    private final boolean isStatic;

    private Reflected(String className) throws RuntimeReflectiveOperationException {
        try {
            this.type = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeReflectiveOperationException(e);
        }
        this.target = null;
        this.isStatic = true;
    }

    private Reflected(Object target) {
        this.target = target;
        this.type = typeOf(target);
        this.isStatic = false;
    }

    public static Reflected on(String className) throws RuntimeReflectiveOperationException {
        return new Reflected(className);
    }

    public static Reflected on(Object target) {
        return new Reflected(target);
    }

    public Reflected call(String method, Object... args) {
        Class<?>[] types = types(args);
        return call(method, types, Objects.nonNull(args) ? args : new Object[0]);
    }

    public Reflected call(String method, Class<?>[] types, Object... args) {
        if (types == null)
            throw new IllegalArgumentException("Types cannot be null");

        InvocationPipeline pipeline = EinfprogJRunit.getContext().pipeline();
        Invocation invocation = Invocation.builder()
                .id(UUID.randomUUID())
                .name(method)
                .parameterTypes(List.of(types))
                .returnType(Any.class)
                .arguments(List.of(args))
                .targetClass(type)
                .target(target)
                .build();
        return new Reflected(pipeline.run(invocation).returnValue());
    }

    public Reflected create(Object... args) {
        InvocationPipeline pipeline = EinfprogJRunit.getContext().pipeline();
        Invocation invocation = Invocation.builder()
                .id(UUID.randomUUID())
                .name("<init>")
                .parameterTypes(List.of(types(args)))
                .returnType(Any.class)
                .arguments(List.of(args))
                .targetClass(type)
                .target(null)
                .build();
        return new Reflected(pipeline.run(invocation).returnValue());
    }

    public <T> T get() {
        return (T) target;
    }

    public <T> T as(Class<T> proxyClass) {
        if (!ProxyUtil.isProxyClass(proxyClass))
            throw new IllegalArgumentException("Argument has to be an interface with an @Proxy annotation");
        if (isStatic)
            throw new IllegalCallerException("Cannot wrap a static class in a proxy");
        return ProxyUtil.wrap(target, proxyClass);
    }

    private static Class<?>[] types(Object[] values) {
        if (values == null)
            return new Class<?>[0];
        return Arrays.stream(values).map(Reflected::typeOf).toArray(Class<?>[]::new);
    }

    private static Class<?> typeOf(Object o) {
        if (o == null) return Any.class;

        if (o.getClass() == Boolean.class)
            return boolean.class;
        if (o.getClass() == Byte.class)
            return byte.class;
        if (o.getClass() == Character.class)
            return char.class;
        if (o.getClass() == Short.class)
            return short.class;
        if (o.getClass() == Integer.class)
            return int.class;
        if (o.getClass() == Long.class)
            return long.class;
        if (o.getClass() == Float.class)
            return float.class;
        if (o.getClass() == Double.class)
            return double.class;

        return o.getClass();
    }

}
