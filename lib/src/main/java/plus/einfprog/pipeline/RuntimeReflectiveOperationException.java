package plus.einfprog.pipeline;

public class RuntimeReflectiveOperationException extends RuntimeException {
    public RuntimeReflectiveOperationException(ReflectiveOperationException e) {
        super(e);
    }
}
