package plus.einfprog.reflection;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.ReflectiveException;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Reflected {

    private Class<?> type;
    private Object target;

    private Reflected(String className) throws ReflectiveException {
        try {
            this.type = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectiveException(e);
        }
        this.target = null;
    }

    private Reflected(Object target) {
        this.target = target;
        this.type = typeOf(target);
    }

    public static Reflected on(String className) throws ReflectiveException {
        return new Reflected(className);
    }

    public static Reflected on(Object target) {
        return new Reflected(target);
    }

    public Reflected call(String method, Object... args) {
        Class<?>[] types = types(args);
        return call(method, types, args);
    }

    public Reflected call(String method, Class<?>[] types, Object... args) {
        if (types == null)
            throw new IllegalArgumentException("Types cannot be null");

        Method m = findMatchingMethod(method, types)
                .orElseThrow(() -> new ReflectiveException(String.format("No method %s on %s with parameters %s", method, type.getSimpleName(), Arrays.toString(types))));
        try {
            InvocationPipeline pipeline = EinfprogJRunit.getContext().pipeline();

            UUID traceId = UUID.randomUUID();
            MethodCall mc = pipeline.before().run(new MethodCall(traceId, m, args, type, target));
            Object returnValue = mc.method().invoke(target, mc.args());
            MethodCallResult result = pipeline.after().run(new MethodCallResult(traceId, mc, returnValue));
            return new Reflected(result.returnValue());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveException(e);
        }
    }

    public <T> T get() {
        return (T) target;
    }

    private Optional<Method> findMatchingMethod(String name, Class<?>[] types) {
        try {
            return Optional.of(type.getMethod(name, types));
        } catch (NoSuchMethodException e) {
            Class<?> t = type;
            while (t != null) {
                List<Method> methods = Arrays.stream(t.getDeclaredMethods())
                        .filter(m -> m.getName().equals(name))
                        .filter(m -> match(m.getParameterTypes(), types))
                        .toList();

                Optional<Method> mostSpecific = findMostSpecificMethodByReturnValue(methods);
                if (mostSpecific.isPresent())
                    return mostSpecific;

                t = t.getSuperclass();
            }
        }
        return Optional.empty();
    }

    private static boolean match(Class<?>[] declared, Class<?>[] argumentTypes) {
        for (int i = 0; i < declared.length; i++) {
            if (argumentTypes[i] == Undefined.class)
                continue;

            if (!declared[i].isAssignableFrom(argumentTypes[i]))
                return false;
        }
        return true;
    }

    private static Optional<Method> findMostSpecificMethodByReturnValue(List<Method> methods) {
        List<Method> specificMethods = new ArrayList<>();
        for (Method a : methods) {
            boolean isMostSpecific = true;
            for (Method b : methods) {
                Class<?> retA = a.getReturnType();
                Class<?> retB = b.getReturnType();
                if (a != b && !retA.equals(retB) && retA.isAssignableFrom(retB)) {
                    isMostSpecific = false;
                    break;
                }
            }
            if (isMostSpecific)
                specificMethods.add(a);
        }
        return specificMethods.stream().findAny();
    }

    private static Class<?>[] types(Object[] values) {
        if (values == null)
            return new Class<?>[0];
        return Arrays.stream(values).map(Reflected::typeOf).toArray(Class<?>[]::new);
    }

    private static Class<?> typeOf(Object o) {
        if (o == null) return Undefined.class;

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
