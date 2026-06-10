package plus.einfprog.junit;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import plus.einfprog.Context;
import plus.einfprog.EinfprogJRunit;
import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.collector.LinearEventHistory;
import plus.einfprog.log.format.JsonTraceFormatter;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.intercepter.InvocationTracer;
import plus.einfprog.pipeline.intercepter.MethodResolver;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

public class EinfprogJRunitExtension implements BeforeAllCallback, AutoCloseable, TestExecutionExceptionHandler {

    private final InvocationPipeline pipeline;
    private final InvocationEventCollector collector;
    private final TraceFormatter formatter;

    private EinfprogJRunitExtension(InvocationPipeline pipeline, InvocationEventCollector collector, TraceFormatter formatter) {
        this.pipeline = pipeline;
        this.collector = collector;
        this.formatter = formatter;
    }

    @Override
    public void beforeAll(@NonNull ExtensionContext context) {
        EinfprogJRunit.setContext(new Context(pipeline, collector, formatter));
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

    @Override
    public void handleTestExecutionException(@NonNull ExtensionContext context, Throwable throwable) throws Throwable {
        System.err.println(formatter.format(collector.getTrace()));
        throw throwable;
    }

    public static class Builder {

        private final InvocationPipeline pipeline = InvocationPipeline.empty();
        private final InvocationEventCollector collector = new LinearEventHistory();
        private final TraceFormatter formatter = new JsonTraceFormatter();

        public EinfprogJRunitExtension build() {
            ProxyAutoWrapper autoWrapper = new ProxyAutoWrapper();
            MethodResolver resolver = new MethodResolver();
            InvocationTracer tracer = new InvocationTracer(collector);

            pipeline.before().addLast(tracer);
            pipeline.after().addLast(tracer);
            pipeline.exception().addLast(tracer);

            pipeline.before().addFirst(resolver);
            pipeline.before().addFirst(autoWrapper);
            pipeline.after().addLast(autoWrapper);

            return new EinfprogJRunitExtension(pipeline, collector, formatter);
        }
    }
}
