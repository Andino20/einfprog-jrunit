package plus.einfprog;

import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;

public record Context(InvocationPipeline pipeline,
                      InvocationEventCollector eventCollector,
                      TraceFormatter traceFormatter) {
}
