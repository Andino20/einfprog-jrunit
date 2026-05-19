package plus.einfprog;

public class ReflectiveException extends RuntimeException {

    public ReflectiveException(ReflectiveOperationException e) {
        super(e);
    }

    public ReflectiveException(String message) {
        super(message);
    }

}
