package plus.einfprog.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Thrown when the captured console output does not match the expected output.
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class OutputMismatchException extends EinfprogJRunitException {
    private final String actual;
    private final String expected;
    private final String message;
}