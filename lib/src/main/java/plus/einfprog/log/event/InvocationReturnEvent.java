package plus.einfprog.log.event;

import lombok.Builder;

import java.util.UUID;

/**
 * A method or constructor call that completed without throwing an exception.
 *
 * @param id          the id of the corresponding call event
 * @param returnType  the declared return type of the method ({@code void.class} if it has none),
 *                    or the instantiated class for a constructor
 * @param returnValue {@code toString()} of the returned value or created instance ({@code "null"} for {@code null})
 */
@Builder
public record InvocationReturnEvent(UUID id,
                                    Class<?> returnType,
                                    String returnValue) implements InvocationEvent {

    /**
     * @return true if the invoked method has no return value, in which case {@code returnValue} is meaningless
     */
    public boolean isVoid() {
        return returnType == void.class;
    }

}
