package plus.einfprog.pipeline.intercepter;

import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.event.ConstructorCallEvent;
import plus.einfprog.log.event.MethodCallEvent;
import plus.einfprog.log.event.InvocationReturnEvent;
import plus.einfprog.pipeline.RuntimeReflectiveOperationException;
import plus.einfprog.pipeline.dto.Invocation;
import plus.einfprog.pipeline.dto.InvocationResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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
    public Invocation intercept(Invocation invocation) {
        switch (invocation.executable()) {
            case Method _ -> traceMethod(invocation);
            case Constructor<?> _ -> traceConstructor(invocation);
            case null -> throw new RuntimeReflectiveOperationException("executable of invocation was not resolved");
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
    public InvocationResult intercept(InvocationResult result) {
        eventCollector.event(InvocationReturnEvent.builder()
                .id(result.id().toString())
                .returnValue(safeObjectToString(result.returnValue()))
                .build());
        return result;
    }

    private static String safeObjectToString(Object o) {
        return o == null ? "null" : o.toString();
    }

}
