package cs2030.simulator;

import java.util.Optional;
import cs2030.util.Pair;

class EventStub extends Event {

    EventStub(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>empty(), shop);
        return test;
    }

}
