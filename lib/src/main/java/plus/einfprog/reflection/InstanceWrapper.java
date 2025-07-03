package plus.einfprog.reflection;

import java.lang.reflect.Method;

public class InstanceWrapper {
    private final ClassWrapper clazz;
    private final Object instance;

    protected InstanceWrapper(ClassWrapper clazz, Object instance) {
        this.clazz = clazz;
        this.instance = instance;
    }

    public Object get() {
        return instance;
    }

    public CallResult call(String methodName) throws ReflectionException {
        try {
            Method m = clazz.get().getMethod(methodName);
            return new CallResult(m.invoke(instance));
        } catch (ReflectiveOperationException e) {
            throw ReflectionException.from(e);
        }
    }

    public <T> T callAndCast(String methodName) throws ReflectionException {
        return this.call(methodName).cast();
    }
}
