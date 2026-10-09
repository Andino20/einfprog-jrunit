package plus.einfprog.pipeline;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

import java.util.UUID;

/**
 * The result of an {@link Invocation} that completed without throwing an exception.
 *
 * @param id          the id of the corresponding invocation
 * @param invocation  the invocation that produced this result
 * @param returnValue the value returned by a method or the instance created by a constructor
 */
@With
@Builder
public record InvocationResult(@NonNull UUID id,
                               Invocation invocation,
                               Object returnValue) {

    public static InvocationResult from(Invocation invocation, Object returnValue) {
        return InvocationResult.builder()
                .id(invocation.id())
                .invocation(invocation)
                .returnValue(returnValue)
                .build();
    }
}
