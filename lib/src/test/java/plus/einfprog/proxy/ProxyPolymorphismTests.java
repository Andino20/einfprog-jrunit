package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProxyPolymorphismTests {

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

    public static class Unrelated {

    }

    @Proxy("plus.einfprog.proxy.ProxyPolymorphismTests$Foo")
    interface FooProxy {
        String getName();

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
