package plus.einfprog.log.format;

import plus.einfprog.log.event.InvocationEvent;

import java.util.List;

/**
 * Formats a trace into a string.
 */
public interface TraceFormatter {
    String format(List<InvocationEvent> trace);
}
