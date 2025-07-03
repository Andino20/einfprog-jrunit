package plus.einfprog.reflection;

public class CallResult {
    private Object result;

    public CallResult(Object result) {
        this.result = result;
    }

    public <T> T cast() {
        return (T) result;
    }

    public InstanceWrapper wrap(Class<?> clazz) {
        return new InstanceWrapper(new ClassWrapper(clazz), result);
    }
}
