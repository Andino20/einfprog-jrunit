package plus.einfprog.pipeline.hook;

import plus.einfprog.EinfprogJRunit;
import plus.einfprog.pipeline.TimeoutRunner;
import plus.einfprog.exception.InvocationTimeoutException;
import plus.einfprog.exception.TargetInvocationException;
import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.event.ConstructorCallEvent;
import plus.einfprog.log.event.MethodCallEvent;
import plus.einfprog.log.event.InvocationReturnEvent;
import plus.einfprog.pipeline.Invocation;
import plus.einfprog.pipeline.InvocationResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Converts {@link Invocation} to {@link MethodCallEvent} or {@link ConstructorCallEvent}
 * and {@link InvocationResult} to {@link InvocationReturnEvent}
 * and passes them to the {@link InvocationEventCollector}.
 *
 * <p>Targets, arguments and return values are recorded as strings. Since {@code toString} may be student code,
 * it runs on a separate thread of a shared {@link TimeoutRunner} with the configured timeout:
 * a {@code toString} that doesn't finish in time throws an {@link InvocationTimeoutException},
 * and one that throws an exception is reported as a {@link TargetInvocationException}.</p>
 */
public class InvocationTracer implements BeforeHook, AfterHook {

    private static final TimeoutRunner TIMEOUT_RUNNER = new TimeoutRunner("einfprog-toString");

    private final InvocationEventCollector eventCollector;

    public InvocationTracer(InvocationEventCollector eventCollector) {
        this.eventCollector = eventCollector;
    }

    @Override
    public Invocation apply(Invocation invocation) {
        switch (invocation.executable()) {
            case Method _ -> traceMethod(invocation);
            case Constructor<?> _ -> traceConstructor(invocation);
            case null -> throw new IllegalStateException("executable of invocation was not resolved");
        }
        return invocation;
    }

    private void traceMethod(Invocation invocation) {
        eventCollector.event(MethodCallEvent.builder()
                .id(invocation.id())
                .targetClass(invocation.targetClass())
                .target(invocation.target() == null ? null : safeObjectToStringTimeout(invocation.target()))
                .method(invocation.executableName())
                .parameterTypes(invocation.parameterTypes())
                .arguments(argumentsToString(invocation))
                .build());
    }

    private void traceConstructor(Invocation invocation) {
        eventCollector.event(ConstructorCallEvent.builder()
                .id(invocation.id())
                .targetClass(invocation.targetClass())
                .parameterTypes(invocation.parameterTypes())
                .arguments(argumentsToString(invocation))
                .build());
    }

    @Override
    public InvocationResult apply(InvocationResult result) {
        eventCollector.event(InvocationReturnEvent.builder()
                .id(result.id())
                .returnType(result.invocation().returnType())
                .returnValue(safeObjectToStringTimeout(result.returnValue()))
                .build());
        return result;
    }

    private static List<String> argumentsToString(Invocation invocation) {
        return invocation.arguments().stream().map(InvocationTracer::safeObjectToStringTimeout).toList();
    }

    private static String safeObjectToStringTimeout(Object o) {
        long timeout = EinfprogJRunit.getContext().settings().getTimeout();
        TimeUnit unit = EinfprogJRunit.getContext().settings().getTimeoutUnit();
        try {
            return TIMEOUT_RUNNER.submit(timeout, unit, () -> safeObjectToString(o));
        } catch (TimeoutException e) {
            throw new InvocationTimeoutException(e);
        }
    }

    private static String safeObjectToString(Object o) {
        if (o == null) {
            return "null";
        }

        try {
            if (o instanceof Object[] arr) {
                return Arrays.deepToString(arr);
            } else if (o.getClass().isArray()) { // o is an array of primitives
                Class<?> eClass = o.getClass();
                if (eClass == byte[].class)
                    return Arrays.toString((byte[]) o);
                else if (eClass == short[].class)
                    return Arrays.toString((short[]) o);
                else if (eClass == int[].class)
                    return Arrays.toString((int[]) o);
                else if (eClass == long[].class)
                    return Arrays.toString((long[]) o);
                else if (eClass == char[].class)
                    return Arrays.toString((char[]) o);
                else if (eClass == float[].class)
                    return Arrays.toString((float[]) o);
                else if (eClass == double[].class)
                    return Arrays.toString((double[]) o);
                else if (eClass == boolean[].class)
                    return Arrays.toString((boolean[]) o);
                throw new IllegalStateException("unexpected array type: " + eClass.getName());
            } else {
                return o.toString();
            }
        } catch (Exception e) {
            throw new TargetInvocationException("an exception occurred while invoking the toString method of an object", e);
        }
    }

}
