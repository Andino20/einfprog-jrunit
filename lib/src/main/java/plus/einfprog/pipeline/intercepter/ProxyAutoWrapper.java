package plus.einfprog.pipeline.intercepter;

import plus.einfprog.pipeline.dto.Invocation;
import plus.einfprog.pipeline.dto.InvocationResult;
import plus.einfprog.proxy.ProxyUtil;

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
    public Invocation intercept(Invocation invocation) {
        List<Class<?>> paramTypes = invocation.parameterTypes().stream()
                .<Class<?>>map(ProxyUtil::unwrapClass)
                .toList();
        Class<?> returnType = ProxyUtil.unwrapClass(invocation.returnType());
        Object[] args = unwrapInstances(invocation.arguments().toArray());

        originalReturnTypes.put(invocation.id(), invocation.returnType());
        return invocation
                .withParameterTypes(paramTypes)
                .withReturnType(returnType)
                .withArguments(Arrays.asList(args));
    }

    @Override
    public InvocationResult intercept(InvocationResult result) {
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

    private static Object[] unwrapInstances(Object[] args) {
        if (args == null) return new Object[0];
        return Arrays.stream(args)
                .map(ProxyUtil::unwrap)
                .toArray();
    }
}
