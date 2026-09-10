package plus.einfprog.pipeline.intercepter;

import plus.einfprog.ReflectiveException;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.reflection.Any;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MethodResolver implements BeforeInterceptor {

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Override
    public MethodCall intercept(MethodCall call) {
        Class<?> targetClass = call.targetClass();
        String name = call.methodDescriptor().methodName();
        Class<?>[] types = call.methodDescriptor().type().parameterArray();
        Class<?> returnType = call.methodDescriptor().type().returnType();

        Method m = findMatchingMethod(targetClass, name, returnType, types)
                .orElseThrow(() -> new ReflectiveException(String.format("No method %s on %s with parameters %s and return type %s",
                        name, targetClass.getSimpleName(), Arrays.toString(types), returnType)));
        m.setAccessible(true);

        try {
            MethodHandle handle = lookup.unreflect(m);
            return call.withMethodHandle(handle);
        } catch (IllegalAccessException e) {
            throw new ReflectiveException(e);
        }
    }

    private Optional<Method> findMatchingMethod(Class<?> targetClass, String name, Class<?> returnType, Class<?>[] types) {
        try {
            return Optional.of(targetClass.getMethod(name, types));
        } catch (NoSuchMethodException e) {
            Class<?> t = targetClass;
            while (t != null) {
                List<Method> methods = Arrays.stream(t.getDeclaredMethods())
                        .filter(m -> m.getName().equals(name))
                        .filter(m -> match(m.getReturnType(), returnType))
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

    private static boolean match(Class<?>[] declared, Class<?>[] types) {
        if (declared.length != types.length) return false;

        for (int i = 0; i < declared.length; i++) {
            if (!match(declared[i], types[i])) return false;
        }
        return true;
    }

    private static boolean match(Class<?> a, Class<?> b) {
        return b == Any.class || a == b || a.isAssignableFrom(b);
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

}
