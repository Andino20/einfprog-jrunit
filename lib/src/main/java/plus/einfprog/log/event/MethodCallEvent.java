package plus.einfprog.log.event;

import lombok.Builder;

import java.util.List;

@Builder
public record MethodCallEvent(String id,
                              String method,
                              List<String> types,
                              List<String> arguments,
                              Class<?> clazz,
                              Object target) implements InvocationEvent {
}
