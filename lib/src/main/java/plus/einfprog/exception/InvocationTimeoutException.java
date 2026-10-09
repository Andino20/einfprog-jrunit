package plus.einfprog.exception;

import java.util.concurrent.TimeoutException;

/**
 * Thrown when a call into the target code does not finish within the configured timeout.
 */
public class InvocationTimeoutException extends EinfprogJRunitException {
    public InvocationTimeoutException(TimeoutException e) {
        super(e);
    }
}
