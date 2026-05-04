package plus.einfprog.pipeline.dto;

import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record MethodCallResult(@NonNull UUID id,
                               MethodCall call,
                               Object returnValue) {
}
