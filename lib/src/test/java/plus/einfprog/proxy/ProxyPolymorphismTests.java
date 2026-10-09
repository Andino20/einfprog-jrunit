package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

class ProxyPolymorphismTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @SuppressWarnings("unused")
    public static class Foo {

        public String getName() {
            return "Foo";
        }

        public String concatNames(Foo other) {
            return getName() + other.getName();
        }

    }

    public static class Bar extends Foo {

        @Override
        public String getName() {
            return "Bar";
        }

    }

    @Proxy("plus.einfprog.proxy.ProxyPolymorphismTests$Foo")
    interface FooProxy {
        String concatNames(FooProxy other);
    }

    @Proxy("plus.einfprog.proxy.ProxyPolymorphismTests$Bar")
    interface BarProxy extends FooProxy {
    }

    @Test
    void subclassArgumentTest() {
        FooProxy f = ProxyUtil.create(FooProxy.class);
        BarProxy b = ProxyUtil.create(BarProxy.class);
        Assertions.assertEquals("FooBar", f.concatNames(b));
    }

}
