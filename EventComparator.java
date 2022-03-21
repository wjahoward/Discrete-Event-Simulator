package cs2030.simulator;

import java.util.Comparator;

class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event a, Event b) {
        return Double.compare(a.getEventTime(), b.getEventTime());
    }
}
