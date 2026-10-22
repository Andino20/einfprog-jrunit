package plus.einfprog.pipeline.hook;

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

/**
 * Converts {@link Invocation} to {@link MethodCallEvent} and {@link InvocationResult} to {@link InvocationReturnEvent}
 * and passes them to the {@link InvocationEventCollector}.
 */
public class InvocationTracer implements BeforeHook, AfterHook {

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
                .arguments(invocation.arguments().stream().map(InvocationTracer::safeObjectToString).toList())
                .clazz(invocation.targetClass())
                .target(safeObjectToString(invocation.target()))
                .build());
    }

    private void traceConstructor(Invocation invocation) {
        eventCollector.event(ConstructorCallEvent.builder()
                .id(invocation.id().toString())
                .clazz(invocation.targetClass())
                .types(invocation.parameterTypes())
                .arguments(invocation.arguments().stream().map(InvocationTracer::safeObjectToString).toList())
                .build());
    }

    @Override
    public InvocationResult apply(InvocationResult result) {
        eventCollector.event(InvocationReturnEvent.builder()
                .id(result.id().toString())
                .returnValue(safeObjectToString(result.returnValue()))
                .isVoid(result.invocation().executable() instanceof Method m && m.getReturnType() == void.class)
                .build());
        return result;
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
