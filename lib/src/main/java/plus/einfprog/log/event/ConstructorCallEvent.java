package plus.einfprog.log.event;

import lombok.Builder;

import java.util.List;

@Builder
public record ConstructorCallEvent(String id,
                                   Class<?> clazz,
                                   List<Class<?>> types,
                                   List<String> arguments) implements InvocationEvent {
}
