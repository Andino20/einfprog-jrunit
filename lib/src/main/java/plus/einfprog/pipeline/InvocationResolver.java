package plus.einfprog.pipeline;

import plus.einfprog.exception.TargetNotFoundException;
import plus.einfprog.reflection.Any;

import java.lang.reflect.Executable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Resolves an {@link InvocationRequest} into an {@link Invocation} by finding the matching method,
 * or constructor if the name is {@code "<init>"}. Called by the
 * {@link plus.einfprog.pipeline.InvocationPipeline} with proxies already unwrapped.
 *
 * <p>
 * Tries an exact public lookup first, then searches all declared executables (non-public too,
 * and superclasses for methods, skipping bridge methods). Types match if they are assignable
 * or {@link Any}. If several candidates match, an arbitrary one is chosen; if none does,
 * a {@link TargetNotFoundException} is thrown.
 * </p>
 */
public class InvocationResolver {

    public static Invocation resolve(InvocationRequest request) {
        Class<?> targetClass = request.targetClass();
        String name = request.name();
        Class<?>[] types = request.argumentTypes().toArray(Class<?>[]::new);
        Class<?> returnType = request.returnType();

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

        return Invocation.builder()
                .id(UUID.randomUUID())
                .arguments(request.arguments())
                .targetClass(request.targetClass())
                .target(request.target())
                .executable(exec)
                .build();

    }

    private static Optional<Constructor<?>> findMatchingConstructor(Class<?> targetClass, Class<?>[] types) {
        try {
            return Optional.of(targetClass.getConstructor(types));
        } catch (NoSuchMethodException e) {
            return Arrays.stream(targetClass.getDeclaredConstructors())
                    .filter(c -> match(c.getParameterTypes(), types))
                    .findAny();
        }
    }

    private static Optional<Method> findMatchingMethod(Class<?> targetClass, String name, Class<?> returnType, Class<?>[] types) {
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

    private static Optional<Method> findMatchingMethodWithAny(Class<?> targetClass, String name, Class<?> returnType, Class<?>[] types) {
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
