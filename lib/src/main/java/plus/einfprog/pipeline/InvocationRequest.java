package plus.einfprog.pipeline;

import lombok.Builder;
import lombok.With;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A requested call of a method or constructor, built by a proxy or {@link plus.einfprog.reflection.Reflected}.
 * It may still contain proxies; {@link InvocationPipeline#run(InvocationRequest)} unwraps them and resolves
 * the request into an {@link Invocation}. {@link plus.einfprog.reflection.Any} works as a wildcard type.
 *
 * @param targetClass   the class the executable is looked up on
 * @param target        the instance a method is called on; {@code null} for static methods and constructors
 * @param argumentTypes the requested parameter types
 * @param arguments     the argument values
 * @param returnType    the requested return type; a proxy type (or array of one) wraps the result into that proxy
 * @param name          the method name, or {@code "<init>"} for a constructor
 */
@Builder(toBuilder = true)
@With
public record InvocationRequest(Class<?> targetClass,
                                Object target,
                                List<Class<?>> argumentTypes,
                                List<Object> arguments,
                                Class<?> returnType,
                                String name) {

    /**
     * Creates a request with the signature of the given method, e.g. a method of a proxy interface.
     */
    public static InvocationRequest fromMethod(Method m, Class<?> targetClass, Object target, List<Object> arguments) {
        return InvocationRequest.builder()
                .targetClass(targetClass)
                .target(target)
                .argumentTypes(List.of(m.getParameterTypes()))
                .arguments(arguments)
                .returnType(m.getReturnType())
                .name(m.getName())
                .build();
    }

}
