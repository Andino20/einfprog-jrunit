package plus.einfprog.pipeline;

/**
 * An exception that wraps a {@link ReflectiveOperationException}.
 */
public class ReflectiveException extends RuntimeException {

    public ReflectiveException(ReflectiveOperationException e) {
        super(e);
    }

    public ReflectiveException(String message) {
        super(message);
    }

}
