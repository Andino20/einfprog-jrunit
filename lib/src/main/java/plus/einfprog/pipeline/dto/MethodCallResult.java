package plus.einfprog.pipeline.dto;

import lombok.Builder;
import lombok.With;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@With
@Builder
public record MethodCallResult(@NonNull UUID id,
                               MethodCall call,
                               Object returnValue) {

    public static MethodCallResult from(MethodCall call, Object returnValue) {
        return MethodCallResult.builder()
                .id(call.id())
                .call(call)
                .returnValue(returnValue)
                .build();
    }
}
