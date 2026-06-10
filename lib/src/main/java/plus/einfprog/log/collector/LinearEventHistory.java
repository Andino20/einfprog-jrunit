package plus.einfprog.log.collector;

import plus.einfprog.log.event.InvocationEvent;

import java.util.ArrayList;
import java.util.List;

public class LinearEventHistory implements InvocationEventCollector {

    private final List<InvocationEvent> events = new ArrayList<>();

    @Override
    public void event(InvocationEvent event) {
        this.events.add(event);
    }

    @Override
    public List<InvocationEvent> getTrace() {
        return events;
    }

}
