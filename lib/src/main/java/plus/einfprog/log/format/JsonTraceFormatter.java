package plus.einfprog.log.format;

import plus.einfprog.log.event.ExceptionEvent;
import plus.einfprog.log.event.InvocationEvent;
import plus.einfprog.log.event.MethodCallEvent;
import plus.einfprog.log.event.MethodReturnEvent;

import java.util.List;
import java.util.stream.Collectors;

public class JsonTraceFormatter implements TraceFormatter {

    @Override
    public String format(List<InvocationEvent> trace) {
        return '[' + String.join(",", trace.stream().map(JsonTraceFormatter::eventToJson).toList()) + ']';
    }

    private static String eventToJson(InvocationEvent event) {
        if (event instanceof MethodCallEvent callEvent) {
            return callEventToJson(callEvent);
        } else if (event instanceof MethodReturnEvent returnEvent) {
            return returnEventToJson(returnEvent);
        } else if (event instanceof ExceptionEvent exceptionEvent) {
            return exceptionEventToJson(exceptionEvent);
        }

        throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
    }

    private static String callEventToJson(MethodCallEvent event) {
        if (event == null) {
            return "null";
        }

        String typesJson = formatList(event.types());
        String argumentsJson = formatList(event.arguments());

        String className = event.clazz() != null ? event.clazz().getName() : null;
        String targetStr = event.target() != null ? event.target().toString() : null;

        return """
        {
          "id": %s,
          "method": %s,
          "types": %s,
          "arguments": %s,
          "class": %s,
          "target": %s
        }
        """.stripIndent().formatted(
                quote(event.id()),
                quote(event.method()),
                typesJson,
                argumentsJson,
                quote(className),
                quote(targetStr)
        );
    }

    private static String returnEventToJson(MethodReturnEvent event) {
        if (event == null) {
            return "null";
        }

        return """
        {
          "id": %s,
          "returnValue": %s
        }
        """.stripIndent().formatted(
                quote(event.id()),
                quote(event.returnValue())
        );
    }

    private static String exceptionEventToJson(ExceptionEvent event) {
        if (event == null) {
            return "null";
        }

        Throwable ex = event.exception();

        String exceptionClass = ex != null ? ex.getClass().getName() : null;
        String exceptionMessage = ex != null ? ex.getMessage() : null;
        String location = null;

        if (ex != null && ex.getStackTrace().length > 0) {
            StackTraceElement topFrame = ex.getStackTrace()[0];
            location = topFrame.toString();
        }

        return """
        {
          "exception": %s,
          "message": %s,
          "location": %s
        }
        """.stripIndent().formatted(
                quote(exceptionClass),
                quote(exceptionMessage),
                quote(location)
        );
    }

    private static String quote(String value) {
        return value == null ? "null" : "\"" + escapeJson(value) + "\"";
    }

    private static String formatList(List<String> list) {
        if (list == null) return "null";
        return list.stream()
                .map(JsonTraceFormatter::quote)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

}
