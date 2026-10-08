package plus.einfprog.junit;

import lombok.AllArgsConstructor;
import lombok.With;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import plus.einfprog.Context;
import plus.einfprog.EinfprogJRunit;
import plus.einfprog.Settings;
import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.collector.LinearEventHistory;
import plus.einfprog.log.event.ExceptionEvent;
import plus.einfprog.log.format.JsonTraceFormatter;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

/**
 * This extension serves as the integration point between einfprog-jrunit and JUnit.
 * It hooks into the JUnit test lifecycle and initializes the einfprog-jrunit context.
 */
@AllArgsConstructor
public class EinfprogJRunitExtension implements BeforeEachCallback, AutoCloseable, TestExecutionExceptionHandler {

    @With
    private Supplier<InvocationPipeline> pipeline;
    @With
    private Supplier<InvocationEventCollector> collector;
    @With
    private Supplier<TraceFormatter> formatter;
    @With
    private Settings settings;

    @Override
    public void beforeEach(@NonNull ExtensionContext context) {
        EinfprogJRunit.clearContext();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        EinfprogJRunit.setContext(new Context(
                pipeline.get(),
                collector.get(),
                formatter.get(),
                settings,
                out));
    }

    public static EinfprogJRunitExtension getDefault() {
        return new EinfprogJRunitExtension(
                InvocationPipeline::empty,
                LinearEventHistory::new,
                JsonTraceFormatter::new,
                Settings.getDefault());
    }

    @Override
    public void close() {
        EinfprogJRunit.clearContext();
    }

    @Override
    public void handleTestExecutionException(@NonNull ExtensionContext context, @NonNull Throwable throwable) throws Throwable {
        InvocationEventCollector eventCollector = EinfprogJRunit.getContext().eventCollector();
        eventCollector.event(new ExceptionEvent(throwable));

        TraceFormatter traceFormatter = EinfprogJRunit.getContext().traceFormatter();
        Assertions.fail(traceFormatter.format(eventCollector.getTrace()));
        throw throwable;
    }

    public Context getContext() {
        return EinfprogJRunit.getContext();
    }

}
