package plus.einfprog.pipeline.intercepter;

import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.event.ExceptionEvent;
import plus.einfprog.log.event.MethodCallEvent;
import plus.einfprog.log.event.MethodReturnEvent;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;

public class InvocationTracer implements BeforeInterceptor, AfterInterceptor, ExceptionInterceptor {

    private final InvocationEventCollector eventCollector;

    public InvocationTracer(InvocationEventCollector eventCollector) {
        this.eventCollector = eventCollector;
    }

    @Override
    public MethodCall intercept(MethodCall call) {
        eventCollector.event(MethodCallEvent.builder()
                .id(call.id().toString())
                .method(call.methodDescriptor().methodName())
                .types(call.methodDescriptor().type().parameterList().stream().map(Class::getSimpleName).toList())
                .arguments(call.arguments().stream().map(InvocationTracer::safeObjectToString).toList())
                .clazz(call.targetClass())
                .target(safeObjectToString(call.target()))
                .build());
        return call;
    }

    @Override
    public MethodCallResult intercept(MethodCallResult result) {
        eventCollector.event(MethodReturnEvent.builder()
                .id(result.id().toString())
                .returnValue(safeObjectToString(result.returnValue()))
                .build());
        return result;
    }

    @Override
    public Throwable intercept(Throwable throwable) {
        eventCollector.event(ExceptionEvent.builder()
                .exception(throwable)
                .build());
        return throwable;
    }

    private static String safeObjectToString(Object o) {
        return o == null ? "null" : o.toString();
    }

}
