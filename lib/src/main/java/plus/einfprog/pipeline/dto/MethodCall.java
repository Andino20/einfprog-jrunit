package plus.einfprog.pipeline.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.lang.reflect.Method;
import java.util.UUID;

@With
@Builder
public record MethodCall(@NonNull UUID id,
                         @NonNull Method method,
                         @NonNull Object[] args,
                         @NonNull Class<?> targetClass,
                         Object target) {
}

