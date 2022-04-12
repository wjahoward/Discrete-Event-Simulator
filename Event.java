package cs2030.simulator;

import cs2030.simulator.Customer;

import cs2030.util.Pair;

import java.util.Comparator;

public class Event implements Comparator<Event>{
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

    @Override
    public String toString() {
        return String.format("%.3f %s", this.eventTime, this.getCustomer().toString());
    }
}
