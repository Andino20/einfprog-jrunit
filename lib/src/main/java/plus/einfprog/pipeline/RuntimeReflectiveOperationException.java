package plus.einfprog.pipeline;

/**
 * An exception that wraps a {@link ReflectiveOperationException}.
 */
public class RuntimeReflectiveOperationException extends RuntimeException {

    public RuntimeReflectiveOperationException(ReflectiveOperationException e) {
        super(e);
    }

    public RuntimeReflectiveOperationException(String message) {
        super(message);
    }

}
