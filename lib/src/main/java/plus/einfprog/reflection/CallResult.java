package plus.einfprog.reflection;

public class CallResult {
    private final Object result;

    public CallResult(Object result) {
        this.result = result;
    }

    public Object get() {
        return result;
    }

    public <T> T cast() {
        return (T) result;
    }

    public InstanceWrapper wrap(Class<?> clazz) {
        return new InstanceWrapper(clazz, result);
    }
}
