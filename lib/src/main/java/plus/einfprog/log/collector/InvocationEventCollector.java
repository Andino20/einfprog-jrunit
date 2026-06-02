package plus.einfprog.log.collector;

import plus.einfprog.log.event.InvocationEvent;

import java.util.List;

public interface InvocationEventCollector {

    void event(InvocationEvent event);

    List<InvocationEvent> getTrace();

}
