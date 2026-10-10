package plus.einfprog.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

import static plus.einfprog.reflection.Reflected.on;

class ReflectionTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    private static final String FOO_CLASS_NAME = "plus.einfprog.reflection.ReflectionTests$Foo";

    @SuppressWarnings("unused")
    public static class Foo {

        public static int getFive() {
            return 5;
        }

        public static int add(int a, int b) {
            return a + b;
        }

        public static int add2(Integer a, Integer b) {
            return a + b;
        }

        public static void accept(Object o) {

        }

    }

    @Test
    void testStaticMethodCallOnClass() {
        int result = on(FOO_CLASS_NAME).call("add", 2, 3).get();
        Assertions.assertEquals(5, result);

        int five = on(FOO_CLASS_NAME).call("getFive").get();
        Assertions.assertEquals(5, five);
    }

    @Test
    void testBoxTypeMethodCallOnClass() {
        int result = on(FOO_CLASS_NAME).call("add", Integer.valueOf(2), Integer.valueOf(3)).get();
        Assertions.assertEquals(5, result);

        result = on(FOO_CLASS_NAME).call("add2", new Class[]{Integer.class, Integer.class}, Integer.valueOf(2), Integer.valueOf(3)).get();
        Assertions.assertEquals(5, result);
    }

    @Test
    void testNullAsArgument() {
        Assertions.assertDoesNotThrow(() -> on(FOO_CLASS_NAME).call("accept", (Object) null).get());
    }

}
