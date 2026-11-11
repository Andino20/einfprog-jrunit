package plus.einfprog.pipeline;

import lombok.Builder;
import lombok.NonNull;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Describes a single invocation of an {@link Executable} (a {@link java.lang.reflect.Method} or
 * {@link java.lang.reflect.Constructor}) that is passed through the {@link plus.einfprog.pipeline.InvocationPipeline}.
 *
 * <p>
 * The {@code executableName}, {@code parameterTypes} and {@code returnType} describe the signature that is requested
 * by the caller. The {@code executable} is {@code null} until it has been resolved against the
 * {@code targetClass}. Hooks that need to distinguish between methods and constructors can pattern match
 * against the resolved {@code executable}.
 * </p>
 *
 * @param id             a unique id shared with the corresponding {@link InvocationResult}
 * @param arguments      the argument values passed to the executable
 * @param targetClass    the class on which the executable is resolved
 * @param target         the instance on which a method is invoked, or {@code null} for static methods and constructors
 * @param executable     the resolved executable, or {@code null} if it has not been resolved yet
 */
@Builder
public record Invocation(@NonNull UUID id,
                         List<Object> arguments,
                         Class<?> targetClass,
                         Object target,
                         Executable executable) {

    public List<Class<?>> parameterTypes() {
        return Arrays.asList(executable.getParameterTypes());
    }

    public Class<?> returnType() {
        return executable instanceof Method m ? m.getReturnType() : executable.getDeclaringClass();
    }

    public String executableName() {
        return executable instanceof Method m ? m.getName() : "<init>";
    }

}
