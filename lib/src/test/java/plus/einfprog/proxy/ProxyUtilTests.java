package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

import java.util.Arrays;
import java.util.stream.IntStream;

class ProxyUtilTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();


    static class A {
    }

    @Proxy("plus.einfprog.proxy.ProxyUtilTests$A")
    interface ProxyA {
    }

    @Test
    void flatArrayUnwrapTest() {
        A[] arr = IntStream.range(0, 10).mapToObj(i -> new A()).toArray(A[]::new);
        ProxyA[] proxyArr = Arrays.stream(arr).map(a -> ProxyHelper.wrap(a, ProxyA.class)).toArray(ProxyA[]::new);

        Object unwrapped = ProxyHelper.deepUnwrapProxyArray(proxyArr);

        Assertions.assertNotNull(unwrapped);
        Assertions.assertTrue(unwrapped.getClass().isArray());
        Assertions.assertEquals(A.class, unwrapped.getClass().getComponentType());
        Assertions.assertArrayEquals(arr, (A[]) unwrapped);
    }

    @Test
    void multiDimensionalArrayUnwrapTest() {
        A[][] arr = IntStream.range(0, 10).mapToObj(i -> IntStream.range(0, 10).mapToObj(j -> new A()).toArray(A[]::new)).toArray(A[][]::new);
        ProxyA[][] proxyArr = Arrays.stream(arr).map(a -> Arrays.stream(a).map(a1 -> ProxyHelper.wrap(a1, ProxyA.class)).toArray(ProxyA[]::new)).toArray(ProxyA[][]::new);

        Object unwrapped = ProxyHelper.deepUnwrapProxyArray(proxyArr);

        Assertions.assertNotNull(unwrapped);
        Assertions.assertTrue(unwrapped.getClass().isArray());
        Assertions.assertEquals(A[].class, unwrapped.getClass().getComponentType());
        Assertions.assertArrayEquals(arr, (A[][]) unwrapped);
    }

    @Test
    void wrapFlatNonProxyArrayInProxyArray() {
        A[] arr = IntStream.range(0, 10).mapToObj(i -> new A()).toArray(A[]::new);

        Object wrapped = ProxyHelper.deepWrapAsProxyArray(arr, ProxyA.class);

        Assertions.assertNotNull(wrapped);
        Assertions.assertTrue(wrapped.getClass().isArray());
        Assertions.assertEquals(ProxyA.class, wrapped.getClass().getComponentType());

        ProxyA[] proxyArr = (ProxyA[]) wrapped;
        for (int i = 0; i < proxyArr.length; i++) {
            Assertions.assertInstanceOf(ProxyA.class, proxyArr[i]);
        }
    }

    @Test
    void wrapMultiDimensionalNonProxyArrayInProxyArray() {
        A[][] arr = IntStream.range(0, 10)
                .mapToObj(i -> IntStream.range(0, 10)
                        .mapToObj(j -> new A())
                        .toArray(A[]::new))
                .toArray(A[][]::new);

        Object wrapped = ProxyHelper.deepWrapAsProxyArray(arr, ProxyA.class);

        Assertions.assertNotNull(wrapped);
        Assertions.assertTrue(wrapped.getClass().isArray());
        Assertions.assertEquals(ProxyA[].class, wrapped.getClass().getComponentType());
    }

}
