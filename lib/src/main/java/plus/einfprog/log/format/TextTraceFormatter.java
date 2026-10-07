package plus.einfprog.log.format;

import plus.einfprog.io.EinfprogJRunitAssertionError;
import plus.einfprog.log.event.*;
import plus.einfprog.pipeline.RuntimeReflectiveOperationException;
import plus.einfprog.pipeline.RuntimeTimeoutException;
import plus.einfprog.pipeline.TargetInvocationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Formats a trace as plain text that is meant to be read by students.
 */
public class TextTraceFormatter implements TraceFormatter {

    private static final String INDENT = "  ";

    @Override
    public String format(List<InvocationEvent> trace) {
        Map<String, String> returnValues = new HashMap<>();
        for (InvocationEvent event : trace) {
            if (event instanceof InvocationReturnEvent e) {
                returnValues.put(e.id(), e.returnValue());
            }
        }

        StringBuilder calls = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        int step = 0;
        for (InvocationEvent event : trace) {
            switch (event) {
                case MethodCallEvent e ->
                        calls.append(formatCall(++step, methodCallToString(e), "returned", returnValues.get(e.id())));
                case ConstructorCallEvent e ->
                        calls.append(formatCall(++step, constructorCallToString(e), "created", returnValues.get(e.id())));
                case ExceptionEvent e -> errors.append(indent(describe(e.exception()), INDENT)).append('\n');
                case InvocationReturnEvent _ -> {
                }
                case null -> {
                }
            }
        }

        StringBuilder text = new StringBuilder("Test failed.\n");
        if (!calls.isEmpty()) {
            text.append("\nCalls made to your code, in order:\n").append(calls);
        }
        if (!errors.isEmpty()) {
            text.append("\nWhat went wrong:\n").append(errors);
        }
        return text.toString();
    }

    private static String formatCall(int step, String call, String verb, String returnValue) {
        String outcome = returnValue == null ? "did not finish" : verb + " " + returnValue;
        return "%s%2d. %s\n%s      -> %s\n".formatted(INDENT, step, call, INDENT, outcome);
    }

    private static String methodCallToString(MethodCallEvent event) {
        return targetToString(event) + "." + event.method() + argumentsToString(event.types(), event.arguments());
    }

    private static String constructorCallToString(ConstructorCallEvent event) {
        List<String> types = event.types().stream().map(Class::getSimpleName).toList();
        return "new " + event.clazz().getSimpleName() + argumentsToString(types, event.arguments());
    }

    /**
     * Static methods are shown as {@code Class.method()}, instance methods as {@code object.method()}.
     */
    private static String targetToString(MethodCallEvent event) {
        String className = event.clazz().getSimpleName();
        if (event.target() == null || "null".equals(event.target().toString())) {
            return className;
        }
        // shorten the default Object.toString(), e.g. "pkg.Foo@1b2c3d" to "Foo@1b2c3d"
        String target = event.target().toString();
        String defaultPrefix = event.clazz().getName() + "@";
        return target.startsWith(defaultPrefix) ? className + "@" + target.substring(defaultPrefix.length()) : target;
    }

    private static String argumentsToString(List<String> types, List<String> arguments) {
        return IntStream.range(0, arguments.size())
                .mapToObj(i -> quote(i < types.size() ? types.get(i) : "", arguments.get(i)))
                .collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * Writes strings and chars the way they would appear in Java source code.
     */
    private static String quote(String type, String argument) {
        if ("null".equals(argument)) {
            return argument;
        }
        return switch (type) {
            case "String" -> '"' + argument + '"';
            case "char", "Character" -> "'" + argument + "'";
            default -> argument;
        };
    }

    private static String describe(Throwable throwable) {
        return switch (throwable) {
            case TargetInvocationException e when e.getCause() != null -> describeTargetException(e.getCause());
            case RuntimeTimeoutException _ ->
                    "Your code took too long and was stopped.\nCheck for a loop that never ends.";
            case EinfprogJRunitAssertionError e -> "Your program printed something else than the test expected.\n"
                    + "Expected output:\n" + output(e.getExpected()) + "\n"
                    + "Actual output:\n" + output(e.getActual());
            case AssertionError e -> "The test expected something else:\n" + indent(message(e), INDENT);
            case RuntimeReflectiveOperationException e -> describeReflectiveException(e);
            case null -> "Unknown error.";
            default -> throwable.getClass().getSimpleName() + ": " + message(throwable);
        };
    }

    private static String describeTargetException(Throwable cause) {
        String description = "Your code threw an exception:\n"
                + INDENT + cause.getClass().getName() + ": " + message(cause);
        for (StackTraceElement frame : cause.getStackTrace()) {
            // skip frames inside the JDK so the location points into the student's code
            if (!frame.getClassName().startsWith("java.") && !frame.getClassName().startsWith("jdk.")) {
                return description + "\n" + INDENT + "at " + frame;
            }
        }
        return description;
    }

    private static String describeReflectiveException(RuntimeReflectiveOperationException exception) {
        if (exception.getCause() instanceof ClassNotFoundException e) {
            return "The test could not find the class " + message(e) + ".\n"
                    + "Check that the class exists and that its name and package are spelled exactly as required.";
        }
        Throwable detail = exception.getCause() == null ? exception : exception.getCause();
        return "The test could not find something it needs in your code:\n"
                + INDENT + message(detail) + "\n"
                + "Check that the name, parameter types and return type are exactly as required.";
    }

    private static String output(String output) {
        return indent(output == null || output.isEmpty() ? "(nothing)" : output, INDENT);
    }

    private static String message(Throwable throwable) {
        return throwable.getMessage() == null ? "(no message)" : throwable.getMessage();
    }

    private static String indent(String text, String indent) {
        return text.lines().map(line -> indent + line).collect(Collectors.joining("\n"));
    }

}
