package plus.einfprog.exception;

/**
 * Thrown when an exception occurs during the invocation of a target method.
 * It wraps the original exception, which can be retrieved via {@link #getCause()}.
 */
public class TargetInvocationException extends EinfprogJRunitException {

    public TargetInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

}
