package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

public class ProxyNullTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @SuppressWarnings("unused")
    public static class Foo {
        public Foo(Object o) {
        }

        public Object passBack(Object o) {
            return o;
        }
    }

    @Proxy("plus.einfprog.proxy.ProxyNullTests$Foo")
    interface FooProxy {
        Object passBack(Object o);
    }

    @Test
    void proxyNullTest() {
        FooProxy foo = ProxyUtil.create(FooProxy.class, (Object) null);
        Assertions.assertNull(foo.passBack(null));
    }
}
