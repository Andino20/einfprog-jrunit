package plus.einfprog.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import plus.einfprog.io.Token;
import plus.einfprog.io.diff.Diff;
import plus.einfprog.io.diff.Edit;

import java.util.List;

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
    private final Diff<Token> diff;
}