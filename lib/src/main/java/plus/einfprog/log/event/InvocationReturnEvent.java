package plus.einfprog.log.event;

import lombok.Builder;

/**
 * @param isVoid true if the invoked method has no return value, in which case {@code returnValue} is meaningless
 */
@Builder
public record InvocationReturnEvent(String id,
                                    String returnValue,
                                    boolean isVoid) implements InvocationEvent {
}
