package plus.einfprog.log.format;

import plus.einfprog.log.event.InvocationEvent;

import java.util.List;

public interface TraceFormatter {
    String format(List<InvocationEvent> trace);
}
