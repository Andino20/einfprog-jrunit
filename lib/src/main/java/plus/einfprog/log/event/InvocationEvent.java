package plus.einfprog.log.event;

public sealed interface InvocationEvent permits
        MethodCallEvent, InvocationReturnEvent, ExceptionEvent, ConstructorCallEvent {
}
