package plus.einfprog.pipeline;

import java.util.concurrent.TimeoutException;

public class RuntimeTimeoutException extends RuntimeException {
    public RuntimeTimeoutException(TimeoutException e) {
        super(e);
    }
}
