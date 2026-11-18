package plus.einfprog.log.event;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * A call of a method, recorded before it runs.
 *
 * @param id             the id of the {@link plus.einfprog.pipeline.Invocation}, shared with its {@link InvocationReturnEvent}
 * @param targetClass    the class the method was resolved on
 * @param target         {@code toString()} of the instance the method is called on; {@code null} for static methods
 * @param method         the method name
 * @param parameterTypes the declared parameter types of the method
 * @param arguments      {@code toString()} of each argument ({@code "null"} for {@code null})
 */
@Builder
public record MethodCallEvent(UUID id,
                              Class<?> targetClass,
                              String target,
                              String method,
                              List<Class<?>> parameterTypes,
                              List<String> arguments) implements InvocationEvent {
}
