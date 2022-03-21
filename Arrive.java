package cs2030.simulator;

//import cs2030.util.Pair;

import java.util.Optional;

public class Arrive extends Event {

    private static final double SERVICE_TIME = 1.0;

    Arrive(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Arrive>, Shop> execute(Shop shop) {
        Pair<Optional<Arrive>, Shop> test = Pair.of(Optional.<Arrive>of(new Arrive(super.getCustomer()
                    , super.getEventTime())), shop);
        return test;
    }

    @Override
    public String toString() {
        return String.format("%s arrives", super.toString());
    }
}
