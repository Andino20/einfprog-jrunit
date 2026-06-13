package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

import java.util.stream.IntStream;

class ProxyUtilTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    static class A {
    }

    static class B {
    }

    @Proxy("plus.einfprog.proxy.ProxyUtilTests$A")
    interface ProxyA {
    }

    @Proxy("plus.einfprog.proxy.ProxyUtilTests$B")
    interface ProxyB {
    }

    @Test
    void wrapTest() {
        Assertions.assertInstanceOf(ProxyA.class, ProxyUtil.wrap(new A(), ProxyA.class));
        Assertions.assertNull(ProxyUtil.wrap(null, ProxyA.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProxyUtil.wrap(new A(), Object.class));

        Assertions.assertThrows(NullPointerException.class, () -> ProxyUtil.wrap(new A(), null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProxyUtil.wrap(new A(), ProxyB.class));
    }

    @Test
    void wrapArrayTest() {
        Assertions.assertNull(ProxyUtil.wrapArray(null, ProxyA.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProxyUtil.wrapArray(new A(), ProxyA.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProxyUtil.wrapArray(new A[0], Object.class));
        Assertions.assertThrows(NullPointerException.class, () -> ProxyUtil.wrapArray(new A[0], null));

        A[] arr = IntStream.range(0, 10).mapToObj(i -> new A()).toArray(A[]::new);
        Object wrappedArr = ProxyUtil.wrapArray(arr, ProxyA.class);
        Assertions.assertNotNull(wrappedArr);
        Assertions.assertInstanceOf(ProxyA[].class, wrappedArr);

        A[][] multiDimArr = IntStream.range(0, 10).mapToObj(i -> arr).toArray(A[][]::new);
        Object wrappedMultiDimArray = ProxyUtil.wrapArray(multiDimArr, ProxyA.class);
        Assertions.assertNotNull(wrappedMultiDimArray);
        Assertions.assertInstanceOf(ProxyA[][].class, wrappedMultiDimArray);

        Assertions.assertThrows(IllegalArgumentException.class, () -> ProxyUtil.wrapArray(arr, ProxyB.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProxyUtil.wrapArray(new A[0], ProxyB.class));
    }

    @Test
    void unwrapTest() {
        A target = new A();
        ProxyA proxy = ProxyUtil.wrap(target, ProxyA.class);
        Assertions.assertSame(target, ProxyUtil.unwrap(proxy));
        Assertions.assertSame(target, ProxyUtil.unwrap(target));
        Assertions.assertNull(ProxyUtil.unwrap(null));

        ProxyA[] proxyArr = new ProxyA[]{proxy};
        Assertions.assertInstanceOf(A[].class, ProxyUtil.unwrap(proxyArr));
    }

    @Test
    void unwrapArrayTest() {
        A target = new A();

        ProxyA[] arr = IntStream.range(0, 10).mapToObj(i -> ProxyUtil.wrap(target, ProxyA.class)).toArray(ProxyA[]::new);
        Object unwrappedArr = ProxyUtil.unwrapArray(arr);
        Assertions.assertNotNull(unwrappedArr);
        Assertions.assertInstanceOf(A[].class, unwrappedArr);

        ProxyA[][] multiDimArr = IntStream.range(0, 10).mapToObj(i -> arr).toArray(ProxyA[][]::new);
        Object unwrappedMultiDimArray = ProxyUtil.unwrapArray(multiDimArr);
        Assertions.assertNotNull(unwrappedMultiDimArray);
        Assertions.assertInstanceOf(A[][].class, unwrappedMultiDimArray);
    }

    @Test
    void unwrapClassTest() {
        Assertions.assertEquals(A.class, ProxyUtil.unwrapClass(ProxyA.class));
        Assertions.assertEquals(Object.class, ProxyUtil.unwrapClass(Object.class));
        Assertions.assertEquals(A[].class, ProxyUtil.unwrapClass(ProxyA[].class));
        Assertions.assertEquals(A[][].class, ProxyUtil.unwrapClass(ProxyA[][].class));
        Assertions.assertThrows(NullPointerException.class, () -> ProxyUtil.unwrapClass(null));
    }

    @Test
    void unwrapArrayClassTest() {
        Assertions.assertEquals(A[].class, ProxyUtil.unwrapArrayClass(ProxyA[].class));
        Assertions.assertEquals(A[][].class, ProxyUtil.unwrapArrayClass(ProxyA[][].class));
        Assertions.assertEquals(Object[].class, ProxyUtil.unwrapArrayClass(Object[].class));
        Assertions.assertEquals(Object[][].class, ProxyUtil.unwrapArrayClass(Object[][].class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProxyUtil.unwrapArrayClass(Object.class));
        Assertions.assertThrows(NullPointerException.class, () -> ProxyUtil.unwrapArrayClass(null));
    }

    @Test
    void canWrapTest() {
        Assertions.assertTrue(ProxyUtil.canWrap(A.class, ProxyA.class));
        Assertions.assertTrue(ProxyUtil.canWrap(B.class, ProxyB.class));

        Assertions.assertFalse(ProxyUtil.canWrap(A.class, ProxyB.class));
        Assertions.assertFalse(ProxyUtil.canWrap(B.class, ProxyA.class));
    }

}
