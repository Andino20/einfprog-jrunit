package plus.einfprog.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.ReflectiveException;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.pipeline.TargetInvocationException;
import plus.einfprog.proxy.Proxy;
import plus.einfprog.proxy.ProxyUtil;

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
        Assertions.assertThrows(ReflectiveException.class, foo::missingMethod);
        Assertions.assertThrows(ReflectiveException.class, () -> foo.wrongArguments(42));
    }
}
