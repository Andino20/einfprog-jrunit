package plus.einfprog.exception;

/**
 * Base class of all exceptions thrown by einfprog-jrunit.
 */
public abstract class EinfprogJRunitException extends RuntimeException {

    protected EinfprogJRunitException() {
    }

    protected EinfprogJRunitException(String message) {
        super(message);
    }

    protected EinfprogJRunitException(Throwable cause) {
        super(cause);
    }

    protected EinfprogJRunitException(String message, Throwable cause) {
        super(message, cause);
    }

}
