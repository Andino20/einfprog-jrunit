package plus.einfprog.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import plus.einfprog.io.Input;
import plus.einfprog.io.Token;
import plus.einfprog.io.diff.Diff;

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
    private final Input input;
    private final Diff<Token> diff;
}