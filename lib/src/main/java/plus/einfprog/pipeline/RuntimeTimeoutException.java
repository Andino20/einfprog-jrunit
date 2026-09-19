package plus.einfprog.pipeline;

import java.util.concurrent.TimeoutException;

/**
 * The runtime version of {@link TimeoutException}.
 */
public class RuntimeTimeoutException extends RuntimeException {
    public RuntimeTimeoutException(TimeoutException e) {
        super(e);
    }
}
