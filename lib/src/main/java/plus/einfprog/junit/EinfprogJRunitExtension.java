package plus.einfprog.junit;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import plus.einfprog.Context;
import plus.einfprog.EinfprogJRunit;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.intercepter.MethodResolver;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

public class EinfprogJRunitExtension implements BeforeAllCallback, AutoCloseable {

    private final InvocationPipeline pipeline;

    private EinfprogJRunitExtension(InvocationPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void beforeAll(@NonNull ExtensionContext context) {
        EinfprogJRunit.setContext(new Context(pipeline));
    }

    public static EinfprogJRunitExtension.Builder builder() {
        return new EinfprogJRunitExtension.Builder();
    }

    public static EinfprogJRunitExtension getDefault() {
        return builder().build();
    }

    @Override
    public void close() {
        EinfprogJRunit.clearContext();
    }

    public static class Builder {

        private final InvocationPipeline pipeline = InvocationPipeline.empty();

        public EinfprogJRunitExtension build() {
            ProxyAutoWrapper autoWrapper = new ProxyAutoWrapper();
            MethodResolver resolver = new MethodResolver();

            pipeline.before().addFirst(resolver);
            pipeline.before().addFirst(autoWrapper);
            pipeline.after().addLast(autoWrapper);

            return new EinfprogJRunitExtension(pipeline);
        }
    }
}
