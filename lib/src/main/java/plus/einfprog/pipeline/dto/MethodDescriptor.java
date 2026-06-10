package plus.einfprog.pipeline.dto;

import lombok.Builder;
import lombok.With;

import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

@With
@Builder
public record MethodDescriptor(String methodName,
                               MethodType type) {

    public static MethodDescriptor from(Method method) {
        return MethodDescriptor.builder()
                .methodName(method.getName())
                .type(MethodType.methodType(method.getReturnType(), method.getParameterTypes()))
                .build();
    }

    public static MethodDescriptor from(MethodHandleInfo info) {
        return MethodDescriptor.builder()
                .methodName(info.getName())
                .type(info.getMethodType())
                .build();
    }
}
