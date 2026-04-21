package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SimpleProxyTests {

    @SuppressWarnings("unused")
    public static class Foo {

        private String s = "Default";

        public Foo() {
        }

        public Foo(String s) {
            this.s = s;
        }

        public int bar() {
            return 0;
        }

        public String getS() {
            return s;
        }

        public String appendIntToString(String s, int i) {
            return s + i;
        }

        public Foo concatS(Foo f) {
            return new Foo(this.s + f.getS());
        }

        public int add(int a, int b) {
            return a + b;
        }

    }

    @Proxy("plus.einfprog.proxy.SimpleProxyTests$Foo")
    interface FooProxy {

        int bar();

        String appendIntToString(String s, int i);

        String getS();

        FooProxy concatS(FooProxy f);

        int add(int a, int b);
    }

    @Test
    void invokeProxyMethodTest() {
        FooProxy foo = ProxyUtil.create(FooProxy.class);
        Assertions.assertEquals(0, foo.bar());
    }

    @Test
    void invokeProxyMethodWithParametersTest() {
        FooProxy foo = ProxyUtil.create(FooProxy.class);
        Assertions.assertEquals("Hello 1", foo.appendIntToString("Hello ", 1));
    }

    @Test
    void proxyAutoboxingTest() {
        FooProxy f1 = ProxyUtil.create(FooProxy.class, "Hello ");
        FooProxy f2 = ProxyUtil.create(FooProxy.class, "World");

        FooProxy f3 = f1.concatS(f2);
        Assertions.assertEquals("Hello World", f3.getS());
    }

    @Test
    void proxyAutoboxingTest2() {
        FooProxy f1 = ProxyUtil.create(FooProxy.class);
        Assertions.assertEquals(3, f1.add(Integer.valueOf(1), Integer.valueOf(2)));
    }

}
