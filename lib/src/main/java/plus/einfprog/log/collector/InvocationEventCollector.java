package plus.einfprog.log.collector;

import plus.einfprog.log.event.InvocationEvent;

import java.util.List;

/**
 * A collection of {@link InvocationEvent}s that can be converted to a trace.
 */
public interface InvocationEventCollector {

    void event(InvocationEvent event);

    List<InvocationEvent> getTrace();

}
