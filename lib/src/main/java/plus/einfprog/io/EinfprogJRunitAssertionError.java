package plus.einfprog.io;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class EinfprogJRunitAssertionError extends RuntimeException {
    private final String actual;
    private final String expected;
    private final String message;
}



