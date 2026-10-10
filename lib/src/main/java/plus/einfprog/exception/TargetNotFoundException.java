package plus.einfprog.exception;

import plus.einfprog.pipeline.Invocation;

import java.util.stream.Collectors;

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

    /**
     * Creates the exception for a test that uses the result of a method that returns nothing.
     */
    public static TargetNotFoundException voidResult(Invocation invocation) {
        String parameters = invocation.parameterTypes().stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", ", "(", ")"));
        return new TargetNotFoundException(String.format(
                "Method %s%s in class %s returns nothing (void), but the test expected it to return a value",
                invocation.name(), parameters, invocation.targetClass().getSimpleName()));
    }

}
