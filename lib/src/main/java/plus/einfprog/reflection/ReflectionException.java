package plus.einfprog.reflection;

public class ReflectionException extends RuntimeException {
    public ReflectionException(String message) {
        super(message);
    }

    public static ReflectionException from(ReflectiveOperationException e) {
        return new ReflectionException(e.getMessage());
    }
}
