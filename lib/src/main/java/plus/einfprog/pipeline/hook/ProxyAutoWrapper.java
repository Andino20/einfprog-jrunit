package plus.einfprog.pipeline.hook;

import plus.einfprog.proxy.ProxyUtil;

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
public class ProxyAutoWrapper {

    public static Object wrapReturnValue(Class<?> originalReturnType, Object returnValue) {
        Class<?> baseType = originalReturnType;
        while (baseType.isArray()) {
            baseType = baseType.getComponentType();
        }

        if (ProxyUtil.isProxyClass(baseType)) {
            return originalReturnType.isArray() ?
                    ProxyUtil.wrapArray(returnValue, baseType) :
                    ProxyUtil.wrap(returnValue, baseType);
        }
        return returnValue;
    }

}
