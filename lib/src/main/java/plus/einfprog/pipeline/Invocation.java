package plus.einfprog.pipeline;

import lombok.Builder;
import lombok.NonNull;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * A resolved call of a method or constructor, created by {@link InvocationResolver}
 * and passed to the hooks of the {@link InvocationPipeline}. Proxies are already unwrapped, and the signature
 * accessors describe the resolved {@code executable}, not the request (which may have used
 * {@link plus.einfprog.reflection.Any}).
 *
 * @param id          unique id, shared with the corresponding {@link InvocationResult}
 * @param arguments   the argument values
 * @param targetClass the class the executable was resolved on (not necessarily its declaring class)
 * @param target      the instance a method is called on; {@code null} for static methods and constructors
 * @param executable  the resolved method or constructor
 */
@Builder
public record Invocation(@NonNull UUID id,
                         List<Object> arguments,
                         Class<?> targetClass,
                         Object target,
                         Executable executable) {

    /**
     * @return the declared parameter types of the executable
     */
    public List<Class<?>> parameterTypes() {
        return Arrays.asList(executable.getParameterTypes());
    }

    /**
     * @return the declared return type of a method, or the declaring class for a constructor
     */
    public Class<?> returnType() {
        return executable instanceof Method m ? m.getReturnType() : executable.getDeclaringClass();
    }

    /**
     * @return the name of a method, or {@code "<init>"} for a constructor
     */
    public String executableName() {
        return executable instanceof Method m ? m.getName() : "<init>";
    }

}
