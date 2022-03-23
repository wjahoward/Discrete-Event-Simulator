package cs2030.simulator;

import cs2030.util.Pair;

import java.util.Optional;

public class Serve extends Event {

    Serve(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>of(new Leave(super.getCustomer()
                , super.getEventTime())), shop);
        return test;
    }

    @Override
    public String toString() {
        return String.format("%s leaves", super.toString());
    }
}
