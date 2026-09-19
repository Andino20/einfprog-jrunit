package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.dto.MethodDescriptor;
import plus.einfprog.proxy.ProxyUtil;

import java.lang.invoke.MethodType;
import java.util.*;

/**
 * Performs the conversion between proxies and target objects.
 *
 * <p>
 * To enable method call delegation, all proxy arguments need to be unwrapped to their target objects,
 * as method resolution is based on the type of the arguments.
 * If no unwrapping is performed, the resolution of the target method would fail,
 * as the argument list would not match the method signature.
 * Also, if the expected return type of the call is a proxy, the return value of the target
 * method needs to be wrapped.
 * </p>
 */
public class ProxyAutoWrapper implements BeforeHook, AfterHook {

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
