package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.IntStream;

class AutoWrapperTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    static class Foo {
        public void bar(Foo f) {
        }

        public void barArray(Foo[] f) {
        }

        public Foo getSelf() {
            return this;
        }

        public Foo[] getSelfArray() {
            return new Foo[]{this};
        }
    }

    @Proxy("plus.einfprog.proxy.AutoWrapperTests$Foo")
    interface FooProxy {
        void bar(FooProxy f);

        void barArray(FooProxy[] f);

        FooProxy getSelf();

        FooProxy[] getSelfArray();

    }

    @Test
    void shouldUnwrapArguments() throws NoSuchMethodException {
        Foo arg = new Foo();
        MethodCall call = new MethodCall(
                UUID.randomUUID(),
                new Foo(),
                FooProxy.class.getMethod("bar", FooProxy.class),
                new Object[]{ProxyHelper.wrap(arg, FooProxy.class)});

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        MethodCall unwrappedCall = w.intercept(call);

        Assertions.assertEquals(call.id(), unwrappedCall.id());
        Assertions.assertEquals(call.target(), unwrappedCall.target());
        Assertions.assertEquals(Foo.class.getMethod("bar", Foo.class), unwrappedCall.method());
        Assertions.assertEquals(arg, unwrappedCall.args()[0]);
    }

    @Test
    void shouldWrapReturnValue() throws NoSuchMethodException {
        ProxyAutoWrapper w = new ProxyAutoWrapper();
        Foo target = new Foo();

        MethodCall call = new MethodCall(
                UUID.randomUUID(),
                target,
                FooProxy.class.getMethod("getSelf"),
                new Object[]{});
        call = w.intercept(call);

        MethodCallResult result = new MethodCallResult(
                call.id(),
                call,
                target);
        result = w.intercept(result);

        Assertions.assertInstanceOf(FooProxy.class, result.returnValue());
    }

    @Test
    void shouldUnwrapArrayArguments() throws NoSuchMethodException {
        Foo[] args = IntStream.range(0, 10).mapToObj(i -> new Foo()).toArray(Foo[]::new);
        FooProxy[] argProxies = Arrays.stream(args).map(t -> ProxyHelper.wrap(t, FooProxy.class)).toArray(FooProxy[]::new);

        MethodCall call = new MethodCall(
                UUID.randomUUID(),
                new Foo(),
                FooProxy.class.getMethod("barArray", FooProxy[].class),
                new Object[]{argProxies});

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        MethodCall unwrappedCall = w.intercept(call);

        Assertions.assertArrayEquals(new Class<?>[]{args.getClass()}, unwrappedCall.method().getParameterTypes());
        for (int i = 0; i < args.length; i++) {
            Assertions.assertInstanceOf(Foo.class, unwrappedCall.args()[i]);
        }
    }

}
