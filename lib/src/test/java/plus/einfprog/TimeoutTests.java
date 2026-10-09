package plus.einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.exception.InvocationTimeoutException;
import plus.einfprog.proxy.Proxy;
import plus.einfprog.proxy.ProxyUtil;

import java.util.concurrent.*;

public class TimeoutTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault()
            .withSettings(Settings.getDefault()
                    .withTimeout(10)
                    .withTimeoutUnit(TimeUnit.MILLISECONDS));

    @SuppressWarnings("unused")
    static class Foo {
        public void timeout() {
            while (true)
                ;
        }
    }

    @Proxy("plus.einfprog.TimeoutTests$Foo")
    interface FooProxy {
        void timeout();
    }

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    void proxyCallShouldTimeout() {
        FooProxy foo = ProxyUtil.create(FooProxy.class);
        Assertions.assertThrows(InvocationTimeoutException.class, foo::timeout);
    }
}
