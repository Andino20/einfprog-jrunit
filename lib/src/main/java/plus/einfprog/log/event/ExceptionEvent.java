package plus.einfprog.log.event;

import lombok.Builder;

@Builder
public record ExceptionEvent(Throwable exception) implements InvocationEvent {
}
