package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

@SuppressWarnings("unused")
class AutoWrapperTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    public static class Foo {

        Object received;

        public void bar(Foo f) {
            received = f;
        }

        public void barArray(Foo[] f) {
            received = f;
        }

        public void barArray(Foo[][] f) {
            received = f;
        }

        public void barArray2(int[] a) {
            received = a;
        }

        public Foo getSelf() {
            return this;
        }

        public Foo[] getSelfArray() {
            return new Foo[]{this};
        }

        public Foo[][] getSelfMultiArray() {
            return new Foo[][]{{this}};
        }
    }

    @Proxy("plus.einfprog.proxy.AutoWrapperTests$Foo")
    public interface FooProxy {
        void bar(FooProxy f);

        void barArray(FooProxy[] f);

        void barArray(FooProxy[][] f);

        void barArray2(int[] a);

        FooProxy getSelf();

        FooProxy[] getSelfArray();

        FooProxy[][] getSelfMultiArray();
    }

    private static FooProxy proxyOf(Foo foo) {
        return ProxyUtil.wrap(foo, FooProxy.class);
    }

    @Test
    void shouldUnwrapArguments() {
        Foo target = new Foo();
        Foo arg = new Foo();

        proxyOf(target).bar(proxyOf(arg));

        Assertions.assertSame(arg, target.received);
    }

    @Test
    void shouldUnwrapArrayArguments() {
        Foo target = new Foo();
        Foo[] args = {new Foo(), new Foo()};

        proxyOf(target).barArray(new FooProxy[]{proxyOf(args[0]), proxyOf(args[1])});

        Foo[] received = Assertions.assertInstanceOf(Foo[].class, target.received);
        Assertions.assertArrayEquals(args, received);
    }

    @Test
    void shouldUnwrapMultiDimensionalArrayArguments() {
        Foo target = new Foo();
        Foo[][] args = {{new Foo(), new Foo()}, {new Foo()}};

        proxyOf(target).barArray(new FooProxy[][]{
                {proxyOf(args[0][0]), proxyOf(args[0][1])},
                {proxyOf(args[1][0])}
        });

        Foo[][] received = Assertions.assertInstanceOf(Foo[][].class, target.received);
        Assertions.assertArrayEquals(args, received);
    }

    @Test
    void shouldNotUnwrapNonProxyArrayArguments() {
        Foo target = new Foo();
        int[] args = {1, 2, 3};

        proxyOf(target).barArray2(args);

        Assertions.assertSame(args, target.received);
    }

    @Test
    void shouldWrapReturnValue() {
        Foo target = new Foo();

        FooProxy result = proxyOf(target).getSelf();

        Assertions.assertSame(target, ProxyUtil.unwrap(result));
    }

    @Test
    void shouldWrapArrayReturnValue() {
        Foo target = new Foo();

        FooProxy[] result = proxyOf(target).getSelfArray();

        Assertions.assertEquals(1, result.length);
        Assertions.assertSame(target, ProxyUtil.unwrap(result[0]));
    }

    @Test
    void shouldWrapMultiDimensionalArrayReturnValue() {
        Foo target = new Foo();

        FooProxy[][] result = proxyOf(target).getSelfMultiArray();

        Assertions.assertEquals(1, result.length);
        Assertions.assertEquals(1, result[0].length);
        Assertions.assertSame(target, ProxyUtil.unwrap(result[0][0]));
    }

}
