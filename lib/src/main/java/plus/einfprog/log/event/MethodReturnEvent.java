package plus.einfprog.log.event;

import lombok.Builder;

@Builder
public record MethodReturnEvent(String id,
                                String returnValue) implements InvocationEvent {
}
