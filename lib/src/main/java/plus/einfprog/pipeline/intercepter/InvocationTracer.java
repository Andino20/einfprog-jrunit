package plus.einfprog.pipeline.intercepter;

import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.event.MethodCallEvent;
import plus.einfprog.log.event.MethodReturnEvent;
import plus.einfprog.pipeline.dto.Invocation;
import plus.einfprog.pipeline.dto.InvocationResult;

/**
 * Converts {@link Invocation} to {@link MethodCallEvent} and {@link InvocationResult} to {@link MethodReturnEvent}
 * and passes them to the {@link InvocationEventCollector}.
 */
public class InvocationTracer implements BeforeHook, AfterHook {

    private final InvocationEventCollector eventCollector;

    public InvocationTracer(InvocationEventCollector eventCollector) {
        this.eventCollector = eventCollector;
    }

    @Override
    public Invocation intercept(Invocation invocation) {
        eventCollector.event(MethodCallEvent.builder()
                .id(invocation.id().toString())
                .method(invocation.name())
                .types(invocation.parameterTypes().stream().map(Class::getSimpleName).toList())
                .arguments(invocation.arguments().stream().map(InvocationTracer::safeObjectToString).toList())
                .clazz(invocation.targetClass())
                .target(safeObjectToString(invocation.target()))
                .build());
        return invocation;
    }

    @Override
    public InvocationResult intercept(InvocationResult result) {
        eventCollector.event(MethodReturnEvent.builder()
                .id(result.id().toString())
                .returnValue(safeObjectToString(result.returnValue()))
                .build());
        return result;
    }

    private static String safeObjectToString(Object o) {
        return o == null ? "null" : o.toString();
    }

}
