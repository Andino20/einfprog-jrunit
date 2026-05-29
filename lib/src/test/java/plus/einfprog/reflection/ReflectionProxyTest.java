package plus.einfprog.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.proxy.Proxy;
import plus.einfprog.proxy.ProxyHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static plus.einfprog.reflection.Reflected.*;

class ReflectionProxyTest {

    @RegisterExtension
    private static EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    private static final String FOO_CLASS_NAME = "plus.einfprog.reflection.ReflectionTests$Foo";

    public static class Foo {

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
        int fortytwo = on(FOO_CLASS_NAME).call("bar", proxy).get();
        Assertions.assertEquals(42, fortytwo);
    }

    @Test
    void reflectedShouldWrapProxyReturnValues() {
        // TODO: figure out how to handle proxies as return values of static functions
        // e.g. on(FOO_CLASS_NAME).call("createFoo").as(FooProxy.class);
    }


}
