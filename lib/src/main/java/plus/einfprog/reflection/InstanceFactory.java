package plus.einfprog.reflection;

import java.lang.reflect.Constructor;

public class InstanceFactory {
    private final Class<?> clazz;

    public InstanceFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    public InstanceWrapper make() throws ReflectionException {
        try {
            Constructor<?> cons = clazz.getDeclaredConstructor();
            return new InstanceWrapper(clazz, cons.newInstance());
        } catch (ReflectiveOperationException e) {
            throw ReflectionException.from(e);
        }
    }

}
