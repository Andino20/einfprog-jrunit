package plus.einfprog.pipeline.hook;

import plus.einfprog.exception.TargetNotFoundException;
import plus.einfprog.pipeline.Invocation;
import plus.einfprog.reflection.Any;

import java.lang.reflect.Executable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tries to resolve the target method or constructor based on the requested name, parameter types and return type.
 *
 * <p>
 * First tries an exact lookup of a public method/constructor with the given parameter types.
 * If that fails, it iterates over all declared methods/constructors of the target class
 * (including non-public ones) to check if their signatures match the requested parameter types
 * and return type. For methods, if no match is found, it repeats by looking at the target
 * superclass and so on. Compiler-generated bridge methods are skipped.
 * A {@link TargetNotFoundException} is thrown if no match can be found.
 * If multiple candidates match (e.g. overloads that are only distinguished by an {@link Any}
 * argument), an arbitrary one is chosen.
 * </p>
 *
 * <p>
 * A parameter type matches if it is the exact same type, if the method parameter type is assignable
 * from the provided type, or if the provided type is the {@link Any} wildcard.
 * The return type matches if the requested return type is assignable from the declared one,
 * or if the requested return type is {@link Any}.
 * The return type is not checked by the exact lookup.
 * </p>
 *
 * <p>
 * For constructors the {@link Invocation#name()} has to be {@code "<init>"}.
 * </p>
 */
public class InvocationResolver implements BeforeHook {

    @Override
    public Invocation apply(Invocation invocation) {
        Class<?> targetClass = invocation.targetClass();
        String name = invocation.name();
        Class<?>[] types = invocation.parameterTypes().toArray(Class<?>[]::new);
        Class<?> returnType = invocation.returnType();

        Executable exec;
        if (name.equals("<init>")) {
            exec = findMatchingConstructor(targetClass, types)
                    .orElseThrow(() -> new TargetNotFoundException(String.format("No constructor %s%s",
                            targetClass.getSimpleName(), typesToString(types))));
        } else {
            exec = findMatchingMethod(targetClass, name, returnType, types)
                    .orElseThrow(() -> new TargetNotFoundException(String.format("No method %s %s%s in class %s",
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
            Method m = targetClass.getMethod(name, types);
            if (match(returnType, m.getReturnType())) {
                return Optional.of(m);
            } else {
                return findMatchingMethodWithAny(targetClass, name, returnType, types);
            }
        } catch (NoSuchMethodException e) {
            return findMatchingMethodWithAny(targetClass, name, returnType, types);
        }
    }

    private Optional<Method> findMatchingMethodWithAny(Class<?> targetClass, String name, Class<?> returnType, Class<?>[] types) {
        Class<?> t = targetClass;
        while (t != null) {
            Stream<Method> methods = Arrays.stream(t.getDeclaredMethods())
                    .filter(m -> m.getName().equals(name))
                    .filter(m -> match(returnType, m.getReturnType()))
                    .filter(m -> match(m.getParameterTypes(), types))
                    .filter(m -> !m.isBridge());

            Optional<Method> method = methods.findAny();
            if (method.isPresent()) return method;

            t = t.getSuperclass();
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
        return a == Any.class || b == Any.class || a == b || a.isAssignableFrom(b);
    }

    private static String typesToString(Class<?>[] types) {
        return Arrays.stream(types).map(InvocationResolver::typeToString).collect(Collectors.joining(", ", "(", ")"));
    }

    private static String typeToString(Class<?> type) {
        return type == null || type == Any.class ? "<any type>" : type.getSimpleName();
    }

}
