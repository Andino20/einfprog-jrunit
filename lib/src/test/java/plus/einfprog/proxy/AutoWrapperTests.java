package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.pipeline.dto.MethodCall;
import plus.einfprog.pipeline.dto.MethodCallResult;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

import java.lang.reflect.Method;
import java.util.UUID;

class AutoWrapperTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    static class Foo {
        public void bar(Foo f) {
        }

        public void barArray(Foo[] f) {
        }

        public Foo baz() {
            return new Foo();
        }

        public Foo[] bazArray() {
            return new Foo[]{new Foo()};
        }
    }

    @Proxy("plus.einfprog.proxy.AutoWrapperTests$Foo")
    interface FooProxy {
        void bar(FooProxy f);

        void barArray(FooProxy[] f);

        FooProxy baz();

        FooProxy[] bazArray();

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
    void shouldUnwrapReturnValue() throws NoSuchMethodException {
        Foo arg = new Foo();
        MethodCall call = new MethodCall(
                UUID.randomUUID(),
                new Foo(),
                FooProxy.class.getMethod("baz"),
                new Object[]{});
        MethodCallResult result = new MethodCallResult(
                call.id(), // should not have it's own id as it is always the same as the call
                call,
                ProxyHelper.wrap(arg, FooProxy.class));

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        MethodCall unwrappedCall = w.intercept(call);
    }


}
