package cs2030.simulator;

//import cs2030.util.Pair;

import java.util.Comparator;
import java.util.Optional;

public abstract class Event<T, U> implements Comparator<Event>{
    private final Customer customer;
    private final double eventTime;

    Event(Customer customer, double eventTime) {
        this.customer = customer;
        this.eventTime = eventTime;
    }

    public double getEventTime() {
        return this.eventTime;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public int compare(Event a, Event b) {
        return Double.compare(a.getEventTime(), b.getEventTime());
    }

    abstract Pair<Optional<T>, Shop> execute(Shop shop);

    @Override
    public String toString() {
        return String.format("%.3f %s", this.eventTime, this.getCustomer().toString());
    }
}