package plus.einfprog.proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.pipeline.Invocation;
import plus.einfprog.pipeline.InvocationResult;
import plus.einfprog.pipeline.hook.ProxyAutoWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
class AutoWrapperTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    public static class Foo {

        public void bar(Foo f) {
        }

        public void barArray(Foo[] f) {
        }

        public void barArray(Foo[][] f) {
        }

        public void barArray2(int[] a) {
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

    @Test
    void shouldUnwrapArguments() throws NoSuchMethodException {
        Foo arg = new Foo();
        Invocation call = Invocation.from(FooProxy.class.getMethod("bar", FooProxy.class))
                .withArguments(List.of(ProxyUtil.wrap(arg, FooProxy.class)))
                .withTargetClass(Foo.class)
                .withTarget(new Foo());

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        Invocation unwrappedCall = w.apply(call);

        Assertions.assertEquals(call.id(), unwrappedCall.id());
        Assertions.assertEquals(call.target(), unwrappedCall.target());
        Assertions.assertEquals(arg, unwrappedCall.arguments().getFirst());
    }

    @Test
    void shouldWrapReturnValue() throws NoSuchMethodException {
        ProxyAutoWrapper w = new ProxyAutoWrapper();
        Foo target = new Foo();
        Invocation call = Invocation.from(FooProxy.class.getMethod("getSelf"))
                .withArguments(List.of())
                .withTargetClass(target.getClass())
                .withTarget(target);
        call = w.apply(call);

        InvocationResult result = new InvocationResult(
                call.id(),
                call,
                target);
        result = w.apply(result);

        Assertions.assertInstanceOf(FooProxy.class, result.returnValue());
    }

    @Test
    void shouldUnwrapArrayArguments() throws NoSuchMethodException {
        Foo[] args = IntStream.range(0, 10).mapToObj(i -> new Foo()).toArray(Foo[]::new);
        FooProxy[] argProxies = Arrays.stream(args).map(t -> ProxyUtil.wrap(t, FooProxy.class)).toArray(FooProxy[]::new);

        Invocation call = Invocation.from(FooProxy.class.getMethod("barArray", FooProxy[].class))
                .withArguments(List.of((Object) argProxies))
                .withTargetClass(Foo.class)
                .withTarget(new Foo());

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        Invocation unwrappedCall = w.apply(call);

        Assertions.assertEquals(List.of(args.getClass()), unwrappedCall.parameterTypes());
        Assertions.assertInstanceOf(Foo[].class, unwrappedCall.arguments().getFirst());

        Foo[] unwrappedArgs = (Foo[]) unwrappedCall.arguments().getFirst();
        for (int i = 0; i < args.length; i++) {
            Assertions.assertSame(args[i], unwrappedArgs[i]);
        }
    }

    @Test
    void shouldUnwrapMultiDimensionalArrayArguments() throws NoSuchMethodException {
        Foo[][] args = IntStream.range(0, 10).mapToObj(i -> IntStream.range(0, 10).mapToObj(j -> new Foo()).toArray(Foo[]::new)).toArray(Foo[][]::new);
        FooProxy[][] argProxies = Arrays.stream(args).map(a -> Arrays.stream(a).map(a1 -> ProxyUtil.wrap(a1, FooProxy.class)).toArray(FooProxy[]::new)).toArray(FooProxy[][]::new);


        Invocation call = Invocation.from(FooProxy.class.getMethod("barArray", FooProxy[][].class))
                .withArguments(List.of((Object) argProxies))
                .withTargetClass(Foo.class)
                .withTarget(new Foo());

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        Invocation unwrappedCall = w.apply(call);

        Assertions.assertEquals(List.of(args.getClass()), unwrappedCall.parameterTypes());
        Assertions.assertInstanceOf(Foo[][].class, unwrappedCall.arguments().getFirst());

        for (int x = 0; x < args.length; x++) {
            for (int y = 0; y < args[x].length; y++) {
                Assertions.assertSame(args[x][y], ((Foo[][]) unwrappedCall.arguments().getFirst())[x][y]);
            }
        }
    }

    @Test
    void shouldNotUnwrapNonProxyArrayArguments() throws NoSuchMethodException {
        int[] args = IntStream.range(0, 10).toArray();
        Invocation call = Invocation.from(FooProxy.class.getMethod("barArray2", int[].class))
                .withArguments(List.of(args))
                .withTargetClass(Foo.class)
                .withTarget(new Foo());

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        AtomicReference<Invocation> unwrappedCallReference = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> unwrappedCallReference.set(w.apply(call)));

        Invocation unwrappedCall = unwrappedCallReference.get();
        Assertions.assertNotNull(unwrappedCall.arguments());
        Assertions.assertEquals(1, unwrappedCall.arguments().size());
        Assertions.assertInstanceOf(int[].class, unwrappedCall.arguments().getFirst());
    }

    @Test
    void shouldWrapArrayReturnValue() throws NoSuchMethodException {
        Foo f = new Foo();

        Invocation call = Invocation.from(FooProxy.class.getMethod("getSelfArray"))
                .withArguments(List.of())
                .withTargetClass(Foo.class)
                .withTarget(f);

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        call = w.apply(call);

        InvocationResult result = new InvocationResult(call.id(), call, f.getSelfArray());
        result = w.apply(result);

        Assertions.assertNotNull(result.returnValue());
        Assertions.assertTrue(result.returnValue().getClass().isArray());
        Assertions.assertEquals(FooProxy[].class, result.returnValue().getClass());
    }

    @Test
    void shouldWrapMultiDimensionalArrayReturnValue() throws NoSuchMethodException {
        Foo f = new Foo();
        Invocation call = Invocation.from(FooProxy.class.getMethod("getSelfMultiArray"))
                .withArguments(List.of())
                .withTargetClass(Foo.class)
                .withTarget(f);

        ProxyAutoWrapper w = new ProxyAutoWrapper();
        call = w.apply(call);

        InvocationResult result = new InvocationResult(call.id(), call, f.getSelfMultiArray());
        result = w.apply(result);

        Assertions.assertNotNull(result.returnValue());
        Assertions.assertTrue(result.returnValue().getClass().isArray());
        Assertions.assertEquals(FooProxy[][].class, result.returnValue().getClass());
    }

}
