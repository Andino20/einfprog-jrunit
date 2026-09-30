package plus.einfprog.log.event;

import lombok.Builder;

@Builder
public record InvocationReturnEvent(String id,
                                    String returnValue) implements InvocationEvent {
}
