package plus.einfprog.pipeline.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandles;
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

    public MethodCall withMethodHandle(@NonNull MethodHandle methodHandle) {
        MethodHandleInfo info = MethodHandles.lookup().revealDirect(methodHandle);
        return MethodCall.builder()
                .id(id)
                .methodDescriptor(MethodDescriptor.from(info))
                .methodHandle(methodHandle)
                .arguments(arguments)
                .targetClass(targetClass)
                .target(target)
                .build();
    }

    public static MethodCall from(Method m) {
        return MethodCall.builder()
                .id(UUID.randomUUID())
                .methodDescriptor(MethodDescriptor.from(m))
                .build();
    }
}

