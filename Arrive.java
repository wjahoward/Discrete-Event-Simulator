package cs2030.simulator;

import cs2030.simulator.Arrive;
import cs2030.simulator.Customer;
import cs2030.simulator.Event;
import cs2030.simulator.EventStub;
import cs2030.simulator.Shop;

import cs2030.util.Pair;

import java.util.Optional;

public class Arrive extends Event {

    Arrive(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>of(new Arrive(super.getCustomer()
                    , super.getEventTime())), shop);
        return test;
    }

    @Override
    public String toString() {
        return String.format("%s arrives", super.toString());
    }
}
