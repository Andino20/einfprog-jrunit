package plus.einfprog.pipeline.intercepter;

import plus.einfprog.ReflectiveException;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.proxy.Proxy;
import plus.einfprog.proxy.ProxyHelper;
import plus.einfprog.proxy.TargetInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

public class ProxyAutoWrapper implements BeforeInterceptor, AfterInterceptor {

    private final Map<UUID, Method> originals = new HashMap<>();

    @Override
    public MethodCall intercept(MethodCall call) {
        String name = call.method().getName();
        Class<?>[] paramTypes = unwrapClasses(call.method().getParameterTypes());
        Object[] args = unwrapInstances(call.args());

        try {
            Method m = call.target().getClass().getMethod(name, paramTypes);
            originals.put(call.id(), call.method());
            return new MethodCall(call.id(), call.target(), m, args);
        } catch (NoSuchMethodException e) {
            throw new ReflectiveException(e);
        }
    }

    @Override
    public MethodCallResult intercept(MethodCallResult result) {
        Class<?> expectedReturnType = originals.remove(result.id()).getReturnType();
        if (expectedReturnType.isAnnotationPresent(Proxy.class)) {
            return new MethodCallResult(result.id(), result.call(), ProxyHelper.wrap(result.returnValue(), expectedReturnType));
        }
        return result;
    }

    private static Class<?>[] unwrapClasses(Class<?>[] classes) {
        if (classes == null) return new Class[0];
        return Arrays.stream(classes)
                .map(ProxyAutoWrapper::unwrapClass)
                .toArray(Class<?>[]::new);
    }

    private static Class<?> unwrapClass(Class<?> clazz) {
        try {
            if (clazz.isArray()) {
                int dim = 0;
                Class<?> tmp = clazz;
                while (tmp.isArray()) {
                    tmp = tmp.getComponentType();
                    dim++;
                }

                Proxy pa = tmp.getDeclaredAnnotation(Proxy.class);
                if (pa != null) {
                    Class<?> targetClass = Class.forName(pa.value());
                    for (int i = 0; i < dim; i++) {
                        targetClass = targetClass.arrayType();
                    }
                    return targetClass;
                }
            }

            Proxy pa = clazz.getDeclaredAnnotation(Proxy.class);
            return Objects.nonNull(pa) ?
                    Class.forName(pa.value()) :
                    clazz;
        } catch (ClassNotFoundException e) {
            throw new ReflectiveException(e);
        }
    }

    private static Object[] unwrapInstances(Object[] args) {
        if (args == null) return new Object[0];
        return Arrays.stream(args)
                .map(ProxyAutoWrapper::unwrapInstance)
                .toArray();
    }

    private static Object unwrapInstance(Object o) {
        if (o == null) return null;
        boolean isProxy = java.lang.reflect.Proxy.isProxyClass(o.getClass());
        if (isProxy) {
            InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(o);
            if (handler instanceof TargetInvocationHandler targetHandler) {
                return targetHandler.getTarget();
            }
        }
        return o;
    }


}
