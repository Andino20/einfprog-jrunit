package plus.einfprog.junit;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import plus.einfprog.Context;
import plus.einfprog.EinfprogJRunit;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

public class EinfprogJRunitExtension implements BeforeAllCallback {

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

    public static class Builder {

        private final InvocationPipeline pipeline = InvocationPipeline.empty();

        public EinfprogJRunitExtension build() {
            ProxyAutoWrapper autoWrapper = new ProxyAutoWrapper();
            pipeline.before().addFirst(autoWrapper);
            pipeline.after().addLast(autoWrapper);

            return new EinfprogJRunitExtension(pipeline);
        }
    }
}
