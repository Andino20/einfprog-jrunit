package plus.einfprog.reflection;

public class ClassWrapper {
    private Class<?> clazz;

    public ClassWrapper(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> get() {
        return clazz;
    }

}
