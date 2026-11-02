package plus.einfprog.log.format;

import org.opentest4j.AssertionFailedError;
import org.opentest4j.ValueWrapper;
import plus.einfprog.exception.InvocationTimeoutException;
import plus.einfprog.exception.OutputMismatchException;
import plus.einfprog.exception.TargetInvocationException;
import plus.einfprog.exception.TargetNotFoundException;
import plus.einfprog.io.Input;
import plus.einfprog.io.Token;
import plus.einfprog.io.diff.Edit;
import plus.einfprog.io.diff.EditType;
import plus.einfprog.log.event.*;

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
    private static final String SEPARATOR = "-".repeat(24);

    @Override
    public String format(List<InvocationEvent> trace) {
        Map<String, InvocationReturnEvent> returns = new HashMap<>();
        for (InvocationEvent event : trace) {
            if (event instanceof InvocationReturnEvent e) {
                returns.put(e.id(), e);
            }
        }
        StringBuilder text = new StringBuilder("Test failed.\n");

        InvocationEvent lastEvent = trace.getLast();
        if (lastEvent instanceof ExceptionEvent e) {
            text.append("\nWhat went wrong:\n").append(describe(e.exception())).append('\n');
        }

        StringBuilder calls = new StringBuilder();
        int step = 0;
        for (InvocationEvent event : trace) {
            switch (event) {
                case MethodCallEvent e ->
                        calls.append(formatCall(++step, methodCallToString(e), "returned", returns.get(e.id())));
                case ConstructorCallEvent e ->
                        calls.append(formatCall(++step, constructorCallToString(e), "created", returns.get(e.id())));
                default -> {}
            }
        }
        if (!calls.isEmpty()) {
            text.append("\nCalls made to your code, in order:\n").append(calls);
        }

        return text.toString();
    }

    private static String formatCall(int step, String call, String verb, InvocationReturnEvent returnEvent) {
        String line = "%s%2d. %s\n".formatted(INDENT, step, call);
        // void calls get no outcome line, so actual return values stand out
        if (returnEvent != null && returnEvent.isVoid()) {
            return line;
        }
        String outcome = returnEvent == null ? "did not finish" : verb + " " + returnEvent.returnValue();
        return line + "%s      -> %s\n".formatted(INDENT, outcome);
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
            case InvocationTimeoutException _ ->
                    "Your code took too long and was stopped.\nCheck for a loop that never ends.";
            case OutputMismatchException e -> describeOutputMismatch(e);
            case AssertionFailedError e when e.isExpectedDefined() && e.isActualDefined() -> describeWrongResult(e);
            case AssertionError e -> "The test expected something else:\n" + message(e);
            case TargetNotFoundException e -> describeTargetNotFound(e);
            case null -> "Unknown error.";
            default -> throwable.getClass().getSimpleName() + ": " + message(throwable);
        };
    }

    private static String describeWrongResult(AssertionFailedError error) {
        String description = "The test expected a different result:\n";
        // JUnit puts a custom message in front of the values, e.g. "sum ==> expected: <5> but was: <4>"
        int separator = message(error).indexOf(" ==> ");
        if (separator != -1) {
            description += message(error).substring(0, separator) + "\n";
        }
        return description
                + "Expected: " + valueToString(error.getExpected()) + "\n"
                + "Actual:   " + valueToString(error.getActual());
    }

    private static String valueToString(ValueWrapper value) {
        String type = value.getType() == null ? "" : value.getType().getSimpleName();
        return quote(type, value.getStringRepresentation());
    }

    private static String describeTargetException(Throwable cause) {
        String description = "Your code threw an exception:\n"
                + INDENT + cause.getClass().getName() + ": " + message(cause);
        for (StackTraceElement frame : cause.getStackTrace()) {
            // skip frames inside the JDK so the location points into the student's code
            if (!frame.getClassName().startsWith("java.") && !frame.getClassName().startsWith("jdk.")) {
                return description + "\n" + INDENT + INDENT + "at " + frame;
            }
        }
        return description;
    }

    private static String describeTargetNotFound(TargetNotFoundException exception) {
        if (exception.getCause() instanceof ClassNotFoundException e) {
            return "The test could not find the class " + message(e) + ".\n"
                    + "Check that the class exists and that its name and package are spelled exactly as required.";
        }
        Throwable detail = exception.getCause() == null ? exception : exception.getCause();
        return "The test could not find something it needs in your code:\n"
                + message(detail) + "\n"
                + "Check that the name, parameter types and return type are exactly as required.";
    }

    private static String describeOutputMismatch(OutputMismatchException exception) {
        String description = "Your program printed something different from what the test expected.\n";
        if (exception.getInput() != null) {
            description += describeInput(exception.getInput());
        }
        if (exception.getDiff() == null) {
            return description
                    + "Your output:\n" + output(exception.getActual()) + "\n\n"
                    + "Expected output:\n" + output(exception.getExpected());
        }
        description += "Your output:\n";
        String actual = exception.getActual();
        List<Edit<Token>> edits = exception.getDiff().getEdits();
        int first = IntStream.range(0, edits.size())
                .filter(i -> edits.get(i).type() != EditType.MATCH)
                .findFirst().orElseThrow();
        Token token = edits.get(first).value();

        int start;
        String marker;
        if (edits.get(first).type() == EditType.INSERT) {
            // the missing token only has a position in the expected output, so point at
            // the next token the program did print (or the end of its output)
            start = edits.stream().skip(first)
                    .filter(e -> e.type() != EditType.INSERT)
                    .findFirst()
                    .map(e -> e.value().index())
                    .orElse(actual.stripTrailing().length());
            marker = "^ missing: \"" + token.value() + '"';
        } else {
            start = token.index();
            String carets = "^".repeat(token.length());
            boolean replaced = first + 1 < edits.size() && edits.get(first + 1).type() == EditType.INSERT;
            marker = replaced
                    ? carets + " expected \"" + edits.get(first + 1).value().value() + "\" here"
                    : carets + " not expected";
        }

        // show the output up to the line of the mismatch with the marker underneath
        int lineStart = actual.lastIndexOf('\n', start - 1) + 1;
        int lineEnd = actual.indexOf('\n', start);
        String text = actual.substring(0, lineEnd == -1 ? actual.length() : lineEnd);
        return description + framed(text + "\n" + " ".repeat(start - lineStart) + marker);
    }

    private static String describeInput(Input input) {
        return "Input: " + input.get().stream()
                .map(o -> '"' + o.toString() + '"')
                .collect(Collectors.joining(", ")) + "\n";
    }

    private static String output(String output) {
        return output == null || output.isEmpty() ? "(nothing)" : framed(output);
    }

    /**
     * Puts separator lines above and below the text, so text from the program stands apart
     * from the explanation around it.
     */
    private static String framed(String text) {
        return SEPARATOR + "\n" + text.lines().collect(Collectors.joining("\n")) + "\n" + SEPARATOR;
    }

    private static String message(Throwable throwable) {
        return throwable.getMessage() == null ? "(no message)" : throwable.getMessage();
    }

}
