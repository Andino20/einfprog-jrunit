package plus.einfprog.reflection;

import java.lang.reflect.Method;

public class InstanceWrapper {
    private final Class<?> clazz;
    private final Object instance;

    protected InstanceWrapper(Class<?> clazz, Object instance) {
        this.clazz = clazz;
        this.instance = instance;
    }

    public Object get() {
        return instance;
    }

    public CallResult call(String methodName) throws ReflectionException {
        try {
            Method m = clazz.getMethod(methodName);
            return new CallResult(m.invoke(instance));
        } catch (ReflectiveOperationException e) {
            throw ReflectionException.from(e);
        }
    }
}
