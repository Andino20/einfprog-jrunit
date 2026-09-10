package plus.einfprog.pipeline.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

@With
@Builder
public record MethodCall(@NonNull UUID id,
                         MethodDescriptor methodDescriptor,
                         MethodHandle methodHandle,
                         List<Object> arguments,
                         Class<?> targetClass,
                         Object target,
                         boolean isStatic) {

    public MethodCall withMethodHandle(@NonNull MethodHandle methodHandle) {
        Method m = MethodHandles.reflectAs(Method.class, methodHandle);
        return MethodCall.builder()
                .id(id)
                .methodDescriptor(MethodDescriptor.from(m))
                .methodHandle(methodHandle)
                .arguments(arguments)
                .targetClass(targetClass)
                .target(target)
                .isStatic(Modifier.isStatic(m.getModifiers()))
                .build();
    }

    public static MethodCall from(Method m) {
        return MethodCall.builder()
                .id(UUID.randomUUID())
                .methodDescriptor(MethodDescriptor.from(m))
                .isStatic(Modifier.isStatic(m.getModifiers()))
                .build();
    }
}

