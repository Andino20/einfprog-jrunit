package plus.einfprog.log.event;

public sealed interface InvocationEvent permits MethodCallEvent, MethodReturnEvent, ExceptionEvent {
}
