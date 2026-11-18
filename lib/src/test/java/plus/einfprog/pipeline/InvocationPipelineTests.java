package plus.einfprog.pipeline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;

import java.util.List;

class InvocationPipelineTests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    public static class Foo {
        private final String name;

        public Foo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    void shouldInvokeResolvedMethod() throws NoSuchMethodException {
        InvocationRequest request = InvocationRequest.fromMethod(Foo.class.getMethod("getName"),
                Foo.class,
                new Foo("foo"),
                List.of());

        Object result = InvocationPipeline.empty().run(request).returnValue();

        Assertions.assertEquals("foo", result);
    }

    @Test
    void shouldInvokeResolvedConstructor() {
        InvocationRequest request = InvocationRequest.builder()
                .targetClass(Foo.class)
                .target(null)
                .argumentTypes(List.of(String.class))
                .arguments(List.of("foo"))
                .returnType(Foo.class)
                .name("<init>")
                .build();

        Object result = InvocationPipeline.empty().run(request).returnValue();

        Assertions.assertInstanceOf(Foo.class, result);
        Assertions.assertEquals("foo", ((Foo) result).getName());
    }
}
