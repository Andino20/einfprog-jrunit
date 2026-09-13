package plus.einfprog.pipeline;

public class TargetInvocationException extends RuntimeException {

    private Throwable cause;

    public TargetInvocationException(String message, Throwable cause) {
        super(message);
    }

    public Throwable getCause() {
        return cause;
    }
}
