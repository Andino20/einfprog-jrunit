package plus.einfprog.log.format;

import org.opentest4j.AssertionFailedError;
import plus.einfprog.exception.InvocationTimeoutException;
import plus.einfprog.exception.OutputMismatchException;
import plus.einfprog.exception.TargetInvocationException;
import plus.einfprog.exception.TargetNotFoundException;
import plus.einfprog.log.event.*;

import java.util.*;
import java.util.stream.Collectors;

public class HumanReadableTraceFormatter implements TraceFormatter {

    private static final String BIG_SEPARATOR = "=".repeat(50);
    private static final String SMALL_SEPARATOR = "-".repeat(50);

    private StringBuilder feedback;

    @Override
    public String format(List<InvocationEvent> trace) {
        feedback = new StringBuilder("Test failed.\n\n");

        findTerminationEvent(trace).ifPresent(this::describeException);

        Map<UUID, InvocationReturnEvent> returns = getReturnEvents(trace);
        List<String> calls = new ArrayList<>();
        int step = 0;
        for (InvocationEvent event : trace) {
            switch (event) {
                case MethodCallEvent e -> calls.add(describeMethodCall(++step, e, returns.get(e.id())));
                case ConstructorCallEvent e -> calls.add(describeConstructorCall(++step, e, returns.get(e.id())));
                case null, default -> {
                }
            }
        }
        if (!calls.isEmpty()) {
            feedback.append("\nCalls made to your code (most recent first):\n")
                    .append(String.join("\n", calls.reversed())).append('\n');
        }
        return feedback.toString();
    }

    private String describeMethodCall(int step, MethodCallEvent e, InvocationReturnEvent r) {
        StringBuilder sb = new StringBuilder()
                .append(step).append(".\t")
                .append(e.target() == null ? e.targetClass().getSimpleName() : e.target())
                .append('.')
                .append(e.method());
        String args = e.arguments().stream().collect(Collectors.joining(", ", "(", ")"));
        sb.append(args);
        sb.append(describeReturnValue(r));
        return sb.toString();
    }

    private String describeConstructorCall(int step, ConstructorCallEvent e, InvocationReturnEvent r) {
        StringBuilder sb = new StringBuilder()
                .append(step).append(".\t")
                .append("new ")
                .append(e.targetClass().getSimpleName());
        String args = e.arguments().stream().collect(Collectors.joining(", ", "(", ")"));
        sb.append(args);
        sb.append(describeReturnValue(r));
        return sb.toString();
    }

    private String describeReturnValue(InvocationReturnEvent r) {
        if (r == null) {
            return "\n\t-> did not finish";
        } else if (!r.isVoid()) {
            return "\n\t-> " + r.returnValue();
        }
        return "";
    }

    private void describeException(ExceptionEvent event) {
        switch (event.exception()) {
            case TargetNotFoundException e -> describeTargetNotFoundException(e);
            case TargetInvocationException e -> describeTargetInvocationException(e);
            case InvocationTimeoutException _ ->
                    feedback.append("Your code took too long to execute and was stopped (timeout).\nCheck for a loop that never ends.\n");
            case OutputMismatchException e -> describeOutputMismatch(e);
            case AssertionFailedError e when e.isExpectedDefined() && e.isActualDefined() -> describeAssertionFailed(e);
            case AssertionError e -> feedback.append("The test expected something else:\n").append(message(e)).append('\n');
            case null -> feedback.append("Unexpected exception.\n");
            default ->
                    feedback.append(event.exception().getClass().getSimpleName()).append(": ").append(message(event.exception())).append('\n');
        }
    }

    private void describeTargetNotFoundException(TargetNotFoundException exception) {
        if (exception.getCause() instanceof ClassNotFoundException e) {
            feedback.append("The test could not find the class ")
                    .append(message(e))
                    .append(".\n")
                    .append("Check that the class exists and that its name and package are spelled exactly as required.\n");
        } else {
            Throwable detail = exception.getCause() == null ? exception : exception.getCause();
            feedback.append("The test could not find something it needs in your code:\n")
                    .append(message(detail))
                    .append("\n")
                    .append("Check that the name, parameter types and return type are exactly as required.\n");
        }
    }

    private void describeTargetInvocationException(TargetInvocationException e) {
        Throwable cause = Objects.requireNonNull(e.getCause(), "cause must not be null");
        feedback.append("Your code threw an unexpected exception: ").append(cause.getClass().getSimpleName()).append('\n');
        String location = findLocationInTarget(cause)
                .map(frame -> "\n\tat " + frame.getClassName() + "." + frame.getMethodName()
                        + "(" + frame.getFileName() + ":" + frame.getLineNumber() + ")")
                .orElse("");
        feedback.append(block(message(cause) + location));
    }

    /**
     * Finds the first stack frame outside the JDK, so the location points into the student's code.
     */
    private static Optional<StackTraceElement> findLocationInTarget(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .filter(frame -> !frame.getClassName().startsWith("java.") && !frame.getClassName().startsWith("jdk."))
                .findFirst();
    }

    private void describeOutputMismatch(OutputMismatchException e) {
        feedback.append("Your program printed something different from what the test expected.\n");

        String expectedBlock = "Expected (this is to be done):\n"
                + SMALL_SEPARATOR + "\n"
                + e.getExpected() + "\n";
        String actualBlock = "Found (this is your output):\n"
                + SMALL_SEPARATOR + "\n"
                + e.getActual() + "\n";

        feedback.append(block(expectedBlock + BIG_SEPARATOR + "\n" + actualBlock));

        if (e.getInput() != null) {
            feedback.append("Input to your program:").append('\n').append(SMALL_SEPARATOR).append('\n');

            List<Object> inputs = e.getInput().get();
            for (Object o : inputs) {
                feedback.append(o).append('\n');
            }

            feedback.append(BIG_SEPARATOR).append('\n');
        }
    }

    private void describeAssertionFailed(AssertionFailedError e) {
        feedback.append("The tests expected a different result:\n");

        int separator = message(e).indexOf(" ==> ");
        if (separator != -1) {
            feedback.append(message(e), 0, separator).append('\n');
        }

        String expected = e.getExpected().getStringRepresentation();
        String actual = e.getActual().getStringRepresentation();
        feedback.append(BIG_SEPARATOR).append('\n');
        feedback.append("Expected: ").append(expected).append('\n');
        feedback.append("Actual:   ").append(actual).append('\n');
        feedback.append(BIG_SEPARATOR).append('\n');
    }

    private static Map<UUID, InvocationReturnEvent> getReturnEvents(List<InvocationEvent> trace) {
        return trace.stream()
                .filter(InvocationReturnEvent.class::isInstance)
                .map(InvocationReturnEvent.class::cast)
                .collect(HashMap::new,
                        (m, e) -> m.put(e.id(), e), HashMap::putAll);
    }

    private static Optional<ExceptionEvent> findTerminationEvent(List<InvocationEvent> trace) {
        if (trace.isEmpty()) return Optional.empty();

        InvocationEvent lastEvent = trace.getLast();
        return lastEvent instanceof ExceptionEvent e ? Optional.of(e) : Optional.empty();
    }

    private static String block(String message) {
        return BIG_SEPARATOR + "\n" + message + "\n" + BIG_SEPARATOR + "\n";
    }

    private String message(Throwable throwable) {
        return throwable.getMessage() == null ? "(no message)" : throwable.getMessage();
    }

}
