import java.lang.reflect.Method;

void main() {
    Class<?> myClass = this.getClass();
    IO.println("getName:\t" + myClass.getName());
    IO.println("getSimpleName:\t" + myClass.getSimpleName());
    IO.println("getCanonicalName:\t" + myClass.getCanonicalName());
    IO.println("getPackage:\t" + myClass.getPackage());


    IO.println("getMethods() (does not list 'main'):");
    Method[] methods = myClass.getMethods();
    for (Method m : methods) {
        IO.println('\t' + m.getName());
    }

    IO.println("getDeclaredMethods() (lists 'main'):");
    methods = myClass.getDeclaredMethods();
    for (Method m : methods) {
        IO.println('\t' + m.getName());
    }
}

int add(int a, int b) {
    return a + b;
}

void helloWorld() {
    IO.println("Hello World!");
}

void thisThrows(Throwable t) throws Throwable {
    throw t;
}