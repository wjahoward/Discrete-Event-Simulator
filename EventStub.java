package cs2030.simulator;

import java.util.Optional;
import cs2030.util.Pair;

class EventStub implements Event {
    private final Customer customer;
    private final double eventTime;

    EventStub(Customer customer, double eventTime) {
        this.customer = customer;
        this.eventTime = eventTime;
    }

    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>empty(), shop);
        return test;
    }

    @Override
    public double getEventTime() {
        return this.eventTime;
    }

    @Override
    public String toString() {
        return String.format("%.3f", this.eventTime);
    }
}
