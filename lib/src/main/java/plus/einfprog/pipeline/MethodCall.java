package plus.einfprog.pipeline;

import java.lang.reflect.Method;

public record MethodCall(Object target, Method method, Object[] args) {
}
