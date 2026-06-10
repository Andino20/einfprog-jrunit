package plus.einfprog.reflection;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.ReflectiveException;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodDescriptor;
import plus.einfprog.proxy.ProxyHelper;

import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Reflected {

    private final Class<?> type;
    private final Object target;
    private final boolean isStatic;

    private Reflected(String className) throws ReflectiveException {
        try {
            this.type = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectiveException(e);
        }
        this.target = null;
        this.isStatic = true;
    }

    private Reflected(Object target) {
        this.target = target;
        this.type = typeOf(target);
        this.isStatic = false;
    }

    public static Reflected on(String className) throws ReflectiveException {
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
        MethodCall mc = pipeline.before().run(MethodCall.builder()
                .id(UUID.randomUUID())
                .methodDescriptor(MethodDescriptor.builder()
                        .methodName(method)
                        .type(MethodType.methodType(Any.class, types))
                        .build())
                .arguments(List.of(args))
                .targetClass(type)
                .target(target)
                .build());
        return new Reflected(pipeline.run(mc).returnValue());
    }

    public <T> T get() {
        return (T) target;
    }

    public <T> T as(Class<T> proxyClass) {
        if (!ProxyHelper.isProxyClass(proxyClass))
            throw new IllegalArgumentException("Argument has to be an interface with an @Proxy annotation");
        if (isStatic)
            throw new IllegalCallerException("Cannot wrap a static class in a proxy");
        return ProxyHelper.wrap(target, proxyClass);
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
