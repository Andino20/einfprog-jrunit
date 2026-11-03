package plus.einfprog.junit;

import lombok.AllArgsConstructor;
import lombok.With;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;
import plus.einfprog.Context;
import plus.einfprog.EinfprogJRunit;
import plus.einfprog.Settings;
import plus.einfprog.log.collector.InvocationEventCollector;
import plus.einfprog.log.collector.LinearEventHistory;
import plus.einfprog.log.format.TextTraceFormatter;
import plus.einfprog.log.format.TraceFormatter;
import plus.einfprog.pipeline.InvocationPipeline;

import java.util.function.Supplier;

/**
 * This extension serves as the integration point between einfprog-jrunit and JUnit.
 * It hooks into the JUnit test lifecycle: it lets {@link EinfprogJRunit#initialize} set up a fresh context before
 * each test and turns test failures into the formatted trace.
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
    public void beforeEach(ExtensionContext context) {
        EinfprogJRunit.initialize(pipeline.get(), collector.get(), formatter.get(), settings);
    }

    public static EinfprogJRunitExtension getDefault() {
        return new EinfprogJRunitExtension(
                InvocationPipeline::empty,
                LinearEventHistory::new,
                TextTraceFormatter::new,
                Settings.getDefault());
    }

    @Override
    public void close() {
        EinfprogJRunit.clearContext();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // A failed assumption means "skip this test", not "this test failed".
        if (throwable instanceof TestAbortedException) {
            throw throwable;
        }

        String message = EinfprogJRunit.recordFailure(throwable);

        // Carry the expected/actual values over so IDEs can still offer their difference viewer.
        if (throwable instanceof AssertionFailedError failure
                && failure.isExpectedDefined() && failure.isActualDefined()) {
            throw new AssertionFailedError(message, failure.getExpected(), failure.getActual(), throwable);
        }
        throw new AssertionFailedError(message, throwable);
    }

    public Context getContext() {
        return EinfprogJRunit.getContext();
    }

}
