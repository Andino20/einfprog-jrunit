package plus.einfprog.testsubjects;

/**
 * Simple class for testing reflection utilities
 */
public class ReflectionTestSubject {

    // Fields with different access modifiers
    public String publicField = "public";
    protected String protectedField = "protected";
    private String privateField = "private";
    static String staticField = "static";
    final String finalField = "final";

    // Constructors
    public ReflectionTestSubject() {}

    public ReflectionTestSubject(String value) {
        this.privateField = value;
    }

    private ReflectionTestSubject(int dummy) {}

    // Methods with different access modifiers
    public void publicMethod() {}

    protected void protectedMethod() {}

    private void privateMethod() {}

    public static void staticMethod() {}

    // Methods with parameters
    public void methodWithParams(String str, int number) {}

    public String methodWithReturn() {
        return "return value";
    }

    // Overloaded methods
    public void overloadedMethod() {}
    public void overloadedMethod(String param) {}
    public void overloadedMethod(int param) {}

    // Generic method
    public <T> T genericMethod(T input) {
        return input;
    }

    // Method that throws exception
    public void throwingMethod() throws Exception {
        throw new RuntimeException("test exception");
    }

    // Getters/setters
    public String getPrivateField() {
        return privateField;
    }

    public void setPrivateField(String privateField) {
        this.privateField = privateField;
    }
}