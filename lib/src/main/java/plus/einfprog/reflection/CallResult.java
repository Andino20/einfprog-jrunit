package plus.einfprog.reflection;

public class CallResult {
    private Object result;

    public CallResult(Object result) {
        this.result = result;
    }

    public <T> T As() {
        return (T) result;
    }
}
