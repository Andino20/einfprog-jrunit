package plus.einfprog.pipeline.dto;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.UUID;

public record MethodCall(@NonNull UUID id,
                         Object target,
                         Method method,
                         Object[] args) {
}
