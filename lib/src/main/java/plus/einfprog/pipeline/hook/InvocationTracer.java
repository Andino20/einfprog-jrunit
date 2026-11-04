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
import java.util.concurrent.*;

/**
 * Converts {@link Invocation} to {@link MethodCallEvent} and {@link InvocationResult} to {@link InvocationReturnEvent}
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
                .id(invocation.id().toString())
                .method(invocation.name())
                .types(invocation.parameterTypes().stream().map(Class::getSimpleName).toList())
                .arguments(invocation.arguments().stream().map(InvocationTracer::safeObjectToStringTimeout).toList())
                .clazz(invocation.targetClass())
                .target(safeObjectToStringTimeout(invocation.target()))
                .build());
    }

    private void traceConstructor(Invocation invocation) {
        eventCollector.event(ConstructorCallEvent.builder()
                .id(invocation.id().toString())
                .clazz(invocation.targetClass())
                .types(invocation.parameterTypes())
                .arguments(invocation.arguments().stream().map(InvocationTracer::safeObjectToStringTimeout).toList())
                .build());
    }

    @Override
    public InvocationResult apply(InvocationResult result) {
        eventCollector.event(InvocationReturnEvent.builder()
                .id(result.id().toString())
                .returnValue(safeObjectToStringTimeout(result.returnValue()))
                .isVoid(result.invocation().executable() instanceof Method m && m.getReturnType() == void.class)
                .build());
        return result;
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
            return o.getClass().isArray() ? Arrays.deepToString((Object[]) o) : o.toString();
        } catch (Exception e) {
            throw new TargetInvocationException("an exception occurred while invoking the toString method of an object", e);
        }
    }

}
