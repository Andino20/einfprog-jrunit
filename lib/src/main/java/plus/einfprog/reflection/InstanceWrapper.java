package plus.einfprog.reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstanceWrapper {
    private ClassWrapper clazz;
    private Object instance;

    protected InstanceWrapper(ClassWrapper clazz, Object instance) {
        this.clazz = clazz;
        this.instance = instance;
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
        return this.call(methodName).As();
    }
}
