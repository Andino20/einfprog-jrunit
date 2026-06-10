package plus.einfprog.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.proxy.Proxy;
import plus.einfprog.proxy.ProxyHelper;

import static plus.einfprog.reflection.Reflected.on;

class ReflectionProxyTest {

    @RegisterExtension
    private static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    private static final String FOO_CLASS_NAME = "plus.einfprog.reflection.ReflectionProxyTest$Foo";

    @SuppressWarnings("unused")
    public static class Foo {

        public Foo getSelf() {
            return this;
        }

        @SuppressWarnings("unused")
        public static int bar(Foo f) {
            return 42;
        }

        public static Foo createFoo() {
            return new Foo();
        }

    }

    @Proxy(FOO_CLASS_NAME)
    public interface FooProxy {
    }

    @Test
    void reflectedShouldUnwrapProxyArguments() {
        FooProxy proxy = ProxyHelper.create(FooProxy.class);
        int fortyTwo = on(FOO_CLASS_NAME).call("bar", proxy).get();
        Assertions.assertEquals(42, fortyTwo);
    }

    @Test
    void reflectedWrapProxyReturnValue() {
        FooProxy foo = on(FOO_CLASS_NAME).call("createFoo").as(FooProxy.class);
        Assertions.assertNotNull(foo);
    }


}
