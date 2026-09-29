package plus.einfprog.pipeline.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * Describes a single invocation of an {@link Executable} (a {@link java.lang.reflect.Method} or
 * {@link java.lang.reflect.Constructor}) that is passed through the {@link plus.einfprog.pipeline.InvocationPipeline}.
 *
 * <p>
 * The {@code name}, {@code parameterTypes} and {@code returnType} describe the signature that is requested
 * by the caller. The {@code executable} is {@code null} until it has been resolved against the
 * {@code targetClass}. Hooks that need to distinguish between methods and constructors can pattern match
 * against the resolved {@code executable}.
 * </p>
 *
 * @param id             a unique id shared with the corresponding {@link InvocationResult}
 * @param name           the name of the requested executable
 * @param parameterTypes the requested parameter types
 * @param returnType     the requested return type
 * @param arguments      the argument values passed to the executable
 * @param targetClass    the class on which the executable is resolved
 * @param target         the instance on which a method is invoked, or {@code null} for static methods and constructors
 * @param executable     the resolved executable, or {@code null} if it has not been resolved yet
 */
@With
@Builder(toBuilder = true)
public record Invocation(@NonNull UUID id,
                         String name,
                         List<Class<?>> parameterTypes,
                         List<Object> arguments,
                         Class<?> returnType,
                         Class<?> targetClass,
                         Object target,
                         Executable executable) {

    /**
     * Sets the resolved executable and updates the signature to match it.
     */
    public Invocation withExecutable(@NonNull Executable executable) {
        return this.toBuilder()
                .name(executable.getName())
                .parameterTypes(List.of(executable.getParameterTypes()))
                .returnType(executable instanceof Method m ? m.getReturnType() : executable.getDeclaringClass())
                .executable(executable)
                .build();
    }

    /**
     * Creates an unresolved invocation that requests the same signature as the given method.
     */
    public static Invocation from(Method m) {
        return Invocation.builder()
                .id(UUID.randomUUID())
                .name(m.getName())
                .parameterTypes(List.of(m.getParameterTypes()))
                .returnType(m.getReturnType())
                .build();
    }
}
