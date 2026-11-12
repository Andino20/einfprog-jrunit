package plus.einfprog.pipeline.hook;

import plus.einfprog.proxy.ProxyUtil;

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
