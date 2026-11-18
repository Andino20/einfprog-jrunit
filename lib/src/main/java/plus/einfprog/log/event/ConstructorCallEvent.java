package plus.einfprog.log.event;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * A call of a constructor, recorded before it runs.
 *
 * @param id             the id of the {@link plus.einfprog.pipeline.Invocation}, shared with its {@link InvocationReturnEvent}
 * @param targetClass    the class that is instantiated
 * @param parameterTypes the declared parameter types of the constructor
 * @param arguments      {@code toString()} of each argument ({@code "null"} for {@code null})
 */
@Builder
public record ConstructorCallEvent(UUID id,
                                   Class<?> targetClass,
                                   List<Class<?>> parameterTypes,
                                   List<String> arguments) implements InvocationEvent {
}
