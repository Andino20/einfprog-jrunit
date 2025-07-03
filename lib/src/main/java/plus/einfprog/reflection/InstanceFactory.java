package plus.einfprog.reflection;

import java.lang.reflect.Constructor;

public class InstanceFactory {
    private ClassWrapper clazz;

    public InstanceFactory(ClassWrapper clazz) {
        this.clazz = clazz;
    }

    public InstanceWrapper make() throws ReflectionException {
        try {
            Constructor<?> cons = clazz.get().getDeclaredConstructor();
            return new InstanceWrapper(clazz, cons.newInstance());
        } catch (ReflectiveOperationException e) {
            throw ReflectionException.from(e);
        }
    }

}
