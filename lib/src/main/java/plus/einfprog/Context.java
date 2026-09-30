package plus.einfprog;

import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.intercepter.InvocationTracer;
import plus.einfprog.pipeline.intercepter.InvocationResolver;
import plus.einfprog.pipeline.intercepter.ProxyAutoWrapper;

import java.io.ByteArrayOutputStream;

/**
 * Shared runtime context containing core instances and configuration used across the testing library.
 * <p>
 * This record serves as a central container holding the execution {@link InvocationPipeline}, logging
 * utilities, global {@link Settings}, and the captured standard output stream. Upon initialization, it automatically
 * configures the default interceptor pipeline with proxy wrapping, method resolution, and invocation tracing.
 * </p>
 *
 * @param pipeline the invocation pipeline managing method execution hooks
 * @param eventCollector the collector responsible for recording invocation events
 * @param traceFormatter the formatter used to render execution traces
 * @param settings the configuration settings governing framework behavior
 * @param out the output stream capturing standard output generated during test execution
 */
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
        setupPipeline(new ProxyAutoWrapper(), new InvocationResolver(), new InvocationTracer(eventCollector));
    }

    private void setupPipeline(ProxyAutoWrapper wrapper,
                               InvocationResolver resolver,
                               InvocationTracer tracer) {
        pipeline.before().addLast(tracer);
        pipeline.before().addFirst(resolver);
        pipeline.before().addFirst(wrapper);

        pipeline.after().addLast(tracer);
        pipeline.after().addLast(wrapper);
    }
}
