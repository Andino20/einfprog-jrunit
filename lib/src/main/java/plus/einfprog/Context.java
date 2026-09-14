package plus.einfprog;

import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.intercepter.InvocationTracer;
import plus.einfprog.pipeline.intercepter.MethodResolver;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

import java.io.ByteArrayOutputStream;

public record Context(InvocationPipeline pipeline,
                      InvocationEventCollector eventCollector,
                      TraceFormatter traceFormatter,
                      Settings settings,
                      ByteArrayOutputStream out) {

    public Context(InvocationPipeline pipeline,
                   InvocationEventCollector eventCollector,
                   TraceFormatter traceFormatter,
                   Settings settings,
                   ByteArrayOutputStream out) {
        this.eventCollector = eventCollector;
        this.traceFormatter = traceFormatter;
        this.pipeline = pipeline;
        this.settings = settings;
        this.out = out;
        setupPipeline(new ProxyAutoWrapper(), new MethodResolver(), new InvocationTracer(eventCollector));
    }

    private void setupPipeline(ProxyAutoWrapper wrapper,
                               MethodResolver resolver,
                               InvocationTracer tracer) {
        pipeline.before().addLast(tracer);
        pipeline.before().addFirst(resolver);
        pipeline.before().addFirst(wrapper);

        pipeline.after().addLast(tracer);
        pipeline.after().addLast(wrapper);
    }
}
