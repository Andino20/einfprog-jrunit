package plus.einfprog;

import lombok.Builder;
import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;

import java.io.ByteArrayOutputStream;

/**
 * Shared runtime context containing core instances and configuration used across the testing library.
 * <p>
 * This class serves as a central container holding the execution {@link InvocationPipeline}, logging
 * utilities, global {@link Settings}, and the captured standard output stream. It is a plain value holder:
 * instances are created through {@code Context.builder()} (usually by
 * {@link EinfprogJRunit#initialize EinfprogJRunit.initialize}, which also registers the default
 * hooks on the pipeline) and are accessed through their getters.
 * </p>
 *
 * @param pipeline       The invocation pipeline managing method execution hooks.
 * @param settings       The configuration settings governing framework behavior.
 * @param out            The output stream capturing standard output generated during test execution.
 * @param eventCollector The collector responsible for recording invocation events.
 * @param traceFormatter The formatter used to render execution traces.
 */
@Builder
public record Context(InvocationPipeline pipeline, Settings settings, ByteArrayOutputStream out,
                      InvocationEventCollector eventCollector, TraceFormatter traceFormatter) {

    /**
     * Returns the event collector cast to the given implementation type.
     *
     * @param type the expected collector type
     * @param <T>  the expected collector type
     * @return the event collector as {@code type}
     * @throws ClassCastException if the collector is not an instance of {@code type}
     */
    public <T extends InvocationEventCollector> T getEventCollector(Class<T> type) {
        return type.cast(eventCollector);
    }

    /**
     * Returns the trace formatter cast to the given implementation type.
     *
     * @param type the expected formatter type
     * @param <T>  the expected formatter type
     * @return the trace formatter as {@code type}
     * @throws ClassCastException if the formatter is not an instance of {@code type}
     */
    public <T extends TraceFormatter> T getTraceFormatter(Class<T> type) {
        return type.cast(traceFormatter);
    }

}
