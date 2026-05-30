package plus.einfprog.pipeline.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@With
@Builder
public record MethodCall(@NonNull UUID id,
                         MethodDescriptor methodDescriptor,
                         MethodHandle methodHandle,
                         List<Object> arguments,
                         Class<?> targetClass,
                         Object target) {

    public static MethodCall from(Method m) {
        return MethodCall.builder()
                .id(UUID.randomUUID())
                .methodDescriptor(MethodDescriptor.builder()
                        .methodName(m.getName())
                        .type(MethodType.methodType(m.getReturnType(), m.getParameterTypes()))
                        .build())
                .build();
    }
}

