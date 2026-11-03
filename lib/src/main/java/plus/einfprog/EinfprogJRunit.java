package plus.einfprog;

import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.event.ExceptionEvent;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;
import plus.einfprog.pipeline.hook.InvocationResolver;
import plus.einfprog.pipeline.hook.InvocationTracer;
import plus.einfprog.pipeline.hook.ProxyAutoWrapper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A globally accessible class for storing the context of the current environment.
 * <p>
 * It also sets up a new context independently of the test framework: test framework integrations
 * (such as {@link plus.einfprog.junit.EinfprogJRunitExtension}) only call {@link #initialize} before each test
 * and {@link #recordFailure} when a test fails.
 * </p>
 */
public class EinfprogJRunit {

    private static Context context;

    private EinfprogJRunit() {
    }

    /**
     * Creates a new context, makes it the current one and returns it. This redirects {@code System.out}
     * into {@link Context#out()} and registers the default hooks on the pipeline.
     *
     * @param pipeline       the pipeline to use; the default hooks are added to it
     * @param eventCollector the collector recording the invocation events
     * @param traceFormatter the formatter rendering the trace when a test fails
     * @param settings       the settings to use
     */
    public static void initialize(InvocationPipeline pipeline, InvocationEventCollector eventCollector,
                                     TraceFormatter traceFormatter, Settings settings) {
        clearContext();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Context newContext = Context.builder()
                .pipeline(pipeline)
                .eventCollector(eventCollector)
                .traceFormatter(traceFormatter)
                .settings(settings)
                .out(out)
                .build();
        registerDefaultHooks(newContext);
        setContext(newContext);
    }

    private static void registerDefaultHooks(Context context) {
        ProxyAutoWrapper wrapper = new ProxyAutoWrapper();
        InvocationResolver resolver = new InvocationResolver();
        InvocationTracer tracer = new InvocationTracer(context.eventCollector());

        InvocationPipeline pipeline = context.pipeline();
        pipeline.before().addLast(tracer);
        pipeline.before().addFirst(resolver);
        pipeline.before().addFirst(wrapper);

        pipeline.after().addLast(tracer);
        pipeline.after().addLast(wrapper);
    }

    /**
     * Records a test failure in the trace of the current context and formats the whole trace.
     * Test framework integrations use the result as the failure message shown to students.
     *
     * @param failure the exception that made the test fail
     * @return the formatted trace, ending with the failure
     * @throws IllegalStateException if no context is set
     */
    public static String recordFailure(Throwable failure) {
        Context current = getContext();
        current.eventCollector().event(new ExceptionEvent(failure));
        return current.traceFormatter().format(current.eventCollector().getTrace());
    }

    public static void setContext(Context context) {
        EinfprogJRunit.context = context;
    }

    public static Context getContext() {
        if (context == null)
            throw new IllegalStateException("No active test context. Register EinfprogJRunitExtension with @RegisterExtension and create proxies inside a test.");
        return context;
    }

    public static void clearContext() {
        context = null;
    }

}
