package plus.einfprog.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.proxy.Proxy;
import plus.einfprog.proxy.ProxyUtil;
import plus.einfprog.reflection.Reflected;

public class ExceptionTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @SuppressWarnings("unused")
    static class Foo {
        public void throwThis(RuntimeException e) {
            throw e;
        }

        public void wrongArguments() {
        }
    }

    @SuppressWarnings("unused")
    static class Bar {
        @Override
        public String toString() {
            throw new RuntimeException("toString threw");
        }
    }

    @Proxy("plus.einfprog.exception.ExceptionTests$Foo")
    interface FooProxy {
        void throwThis(RuntimeException e);

        void missingMethod();

        void wrongArguments(int a);
    }

    @Test
    void shouldWrapInvocationException() {
        FooProxy foo = ProxyUtil.create(FooProxy.class);
        Assertions.assertThrows(TargetInvocationException.class, () -> foo.throwThis(new ArrayIndexOutOfBoundsException()));

        try {
            foo.throwThis(new NullPointerException());
            Assertions.fail("Should have thrown an exception");
        } catch (TargetInvocationException e) {
            Assertions.assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    void missingMethodShouldThrow() {
        FooProxy foo = ProxyUtil.create(FooProxy.class);
        Assertions.assertThrows(TargetNotFoundException.class, foo::missingMethod);
        Assertions.assertThrows(TargetNotFoundException.class, () -> foo.wrongArguments(42));
    }

    @Test
    void shouldWrapToStringException() {
        TargetInvocationException e = Assertions.assertThrows(TargetInvocationException.class, () ->
                Reflected.on("plus.einfprog.exception.ExceptionTests$Bar")
                        .create()
                        .call("toString"));
        Assertions.assertEquals(RuntimeException.class, e.getCause().getClass());
        Assertions.assertEquals("toString threw", e.getCause().getMessage());
    }
}
