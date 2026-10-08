package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.RuntimeReflectiveOperationException;
import plus.einfprog.pipeline.dto.Invocation;
import plus.einfprog.reflection.Any;

import java.lang.reflect.Executable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tries to resolve the target method or constructor based on the requested name, parameter types and return type.
 *
 * <p>
 * Iterates over all methods/constructors of the target class to check if
 * their signatures match a list of argument types and return value.
 * If no match is found, it repeats by looking at the target superclass
 * and so on.
 * An {@link RuntimeReflectiveOperationException} is thrown if no match can be found.
 * If multiple candidates are found in the case of a method,
 * the one with the most specific return type is chosen.
 * </p>
 *
 * <p>
 * A parameter type matches if it is the exact same type, if the method parameter type is assignable
 * from the provided type, or if the provided type is the {@link Any} wildcard.
 * </p>
 *
 * <p>
 * For constructors the {@link Invocation#name()} has to be {@code "<init>"}.
 * </p>
 */
public class InvocationResolver implements BeforeHook {

    @Override
    public Invocation intercept(Invocation invocation) {
        Class<?> targetClass = invocation.targetClass();
        String name = invocation.name();
        Class<?>[] types = invocation.parameterTypes().toArray(Class<?>[]::new);
        Class<?> returnType = invocation.returnType();

        Executable exec;
        if (name.equals("<init>")) {
            exec = findMatchingConstructor(targetClass, types)
                    .orElseThrow(() -> new RuntimeReflectiveOperationException(String.format("No constructor %s%s",
                            targetClass.getSimpleName(), typesToString(types))));
        } else {
            exec = findMatchingMethod(targetClass, name, returnType, types)
                    .orElseThrow(() -> new RuntimeReflectiveOperationException(String.format("No method %s %s%s in class %s",
                            typeToString(returnType), name, typesToString(types), targetClass.getSimpleName())));
        }
        exec.setAccessible(true);
        return invocation.withExecutable(exec);
    }

    private Optional<Constructor<?>> findMatchingConstructor(Class<?> targetClass, Class<?>[] types) {
        try {
            return Optional.of(targetClass.getConstructor(types));
        } catch (NoSuchMethodException e) {
            return Arrays.stream(targetClass.getDeclaredConstructors())
                    .filter(c -> match(c.getParameterTypes(), types))
                    .findAny();
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
                if (a.equals(b) && !retA.equals(retB) && retA.isAssignableFrom(retB)) {
                    isMostSpecific = false;
                    break;
                }
            }
            if (isMostSpecific)
                specificMethods.add(a);
        }
        return specificMethods.stream().findAny();
    }

    private static String typesToString(Class<?>[] types) {
        return Arrays.stream(types).map(InvocationResolver::typeToString).collect(Collectors.joining(", ", "(", ")"));
    }

    private static String typeToString(Class<?> type) {
        return type == null || type == Any.class ? "<any type>" : type.getSimpleName();
    }

}
