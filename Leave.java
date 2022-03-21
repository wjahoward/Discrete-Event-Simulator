package cs2030.simulator;

//import cs2030.util.Pair;

import java.util.Optional;

public class Leave extends Event {

    Leave(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Leave>, Shop> execute(Shop shop) {
        Pair<Optional<Leave>, Shop> test = Pair.of(Optional.<Leave>of(new Leave(super.getCustomer()
                , super.getEventTime())), shop);
        return test;
    }

    @Override
    public String toString() {
        return String.format("%s leaves", super.toString());
    }
}
