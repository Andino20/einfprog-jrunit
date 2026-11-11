package plus.einfprog.pipeline;

import lombok.Builder;
import lombok.With;

import java.lang.reflect.Method;
import java.util.List;

@Builder(toBuilder = true)
@With
public record InvocationRequest(Class<?> targetClass,
                                Object target,
                                List<Class<?>> argumentTypes,
                                List<Object> arguments,
                                Class<?> returnType,
                                String name) {

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
