package cs2030.simulator;

import java.util.Comparator;
import java.util.Optional;
import cs2030.util.Pair;

abstract class Event implements Comparator<Event>{
    private final Customer customer;
    private final double eventTime;

    Event(Customer customer, double eventTime) {
        this.customer = customer;
        this.eventTime = eventTime;
    }

    double getEventTime() {
        return this.eventTime;
    }

    @Override
    public int compare(Event a, Event b) {
        return Double.compare(a.getEventTime(), b.getEventTime());
    }

    abstract Pair<Optional<Event>, Shop> execute(Shop shop);

    @Override
    public String toString() {
        return String.format("%.3f", this.eventTime);
    }
}