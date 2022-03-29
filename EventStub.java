package cs2030.simulator;

import cs2030.simulator.Customer;
import cs2030.simulator.Event;
import cs2030.simulator.Shop;

import cs2030.util.Pair;

import java.util.Optional;

public class EventStub extends Event {

    EventStub(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>empty(), shop);
        return test;
    }

    public double getEventTime() {
        return super.getEventTime();
    }

    public Customer getCustomer() {
        return super.getCustomer();
    }

    @Override
    public String toString() {
        return String.format("%.3f", super.getEventTime());
    }

}
