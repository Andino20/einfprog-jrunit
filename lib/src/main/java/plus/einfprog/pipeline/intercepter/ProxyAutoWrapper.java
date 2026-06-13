package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.dto.MethodDescriptor;
import plus.einfprog.proxy.ProxyUtil;

import java.lang.invoke.MethodType;
import java.util.*;

public class ProxyAutoWrapper implements BeforeInterceptor, AfterInterceptor {

    private final Map<UUID, Class<?>> originalReturnTypes = new HashMap<>();

    @Override
    public MethodCall intercept(MethodCall call) {
        String name = call.methodDescriptor().methodName();
        Class<?>[] paramTypes = unwrapClasses(call.methodDescriptor().type().parameterArray());
        Class<?> returnType = ProxyUtil.unwrapClass(call.methodDescriptor().type().returnType());
        Object[] args = unwrapInstances(call.arguments().toArray());

        originalReturnTypes.put(call.id(), call.methodDescriptor().type().returnType());
        return call.withMethodDescriptor(MethodDescriptor.builder()
                        .methodName(name)
                        .type(MethodType.methodType(returnType, paramTypes))
                        .build())
                .withArguments(List.of(args));
    }

    @Override
    public MethodCallResult intercept(MethodCallResult result) {
        Class<?> expectedReturnType = originalReturnTypes.remove(result.id());
        Class<?> baseType = expectedReturnType;
        while (baseType.isArray()) {
            baseType = baseType.getComponentType();
        }

        if (ProxyUtil.isProxyClass(baseType)) {
            return result.withReturnValue(expectedReturnType.isArray() ?
                    ProxyUtil.wrapArray(result.returnValue(), baseType) :
                    ProxyUtil.wrap(result.returnValue(), baseType));
        }
        return result;
    }

    private static Class<?>[] unwrapClasses(Class<?>[] classes) {
        if (classes == null) return new Class[0];
        return Arrays.stream(classes)
                .map(ProxyUtil::unwrapClass)
                .toArray(Class<?>[]::new);
    }

    private static Object[] unwrapInstances(Object[] args) {
        if (args == null) return new Object[0];
        return Arrays.stream(args)
                .map(ProxyUtil::unwrap)
                .toArray();
    }
}
