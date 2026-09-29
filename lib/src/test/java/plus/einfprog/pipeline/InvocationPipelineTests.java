package plus.einfprog.pipeline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.pipeline.dto.Invocation;

import java.util.List;
import java.util.UUID;

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
        Invocation invocation = Invocation.builder()
                .id(UUID.randomUUID())
                .arguments(List.of())
                .target(new Foo("foo"))
                .build()
                .withExecutable(Foo.class.getMethod("getName"));

        Object result = InvocationPipeline.empty().run(invocation).returnValue();

        Assertions.assertEquals("foo", result);
    }

    @Test
    void shouldInvokeResolvedConstructor() throws NoSuchMethodException {
        Invocation invocation = Invocation.builder()
                .id(UUID.randomUUID())
                .arguments(List.of("foo"))
                .build()
                .withExecutable(Foo.class.getConstructor(String.class));

        Object result = InvocationPipeline.empty().run(invocation).returnValue();

        Assertions.assertInstanceOf(Foo.class, result);
        Assertions.assertEquals("foo", ((Foo) result).getName());
    }

    @Test
    void shouldThrowIfExecutableIsNotResolved() {
        Invocation invocation = Invocation.builder()
                .id(UUID.randomUUID())
                .name("getName")
                .arguments(List.of())
                .target(new Foo("foo"))
                .build();

        Assertions.assertThrows(ReflectiveException.class, () -> InvocationPipeline.empty().run(invocation));
    }

}
