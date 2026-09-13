package plus.einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.pipeline.RuntimeTimeoutException;
import plus.einfprog.proxy.Proxy;
import plus.einfprog.proxy.ProxyUtil;

import java.util.concurrent.*;

public class TimeoutTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

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
    @Timeout(value = 5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void proxyCallShouldTimeout() {
        FooProxy foo = ProxyUtil.create(FooProxy.class);
        Assertions.assertThrows(RuntimeTimeoutException.class, foo::timeout);
    }
}
