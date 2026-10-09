package plus.einfprog.exception;

/**
 * Thrown when a target class, method or constructor cannot be found.
 */
public class TargetNotFoundException extends EinfprogJRunitException {

    public TargetNotFoundException(ReflectiveOperationException e) {
        super(e);
    }

    public TargetNotFoundException(String message) {
        super(message);
    }

}
