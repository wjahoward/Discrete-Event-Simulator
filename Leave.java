package cs2030.simulator;

import cs2030.simulator.Customer;
import cs2030.simulator.Event;
import cs2030.simulator.Shop;

import cs2030.util.Pair;

import java.util.Optional;

public class Leave extends Event {

    public Leave(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        return Pair.of(Optional.<Event>of(new Leave(super.getCustomer()
                , super.getEventTime())), shop);
    }

    @Override
    public String toString() {
        return String.format("%s leaves", super.toString());
    }
}
