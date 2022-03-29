package cs2030.simulator;

import cs2030.simulator.Event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event a, Event b) {
        int result = Double.compare(a.getEventTime(), b.getEventTime());
        return result != 0 ? result : a.getCustomer().getCustomerId() - b.getCustomer().getCustomerId();
    }
}
