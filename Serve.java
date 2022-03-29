package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.Optional;

public class Serve extends Event {

    private final double SERVICE_TIME = 1.0;

    Serve(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>of(new Serve(super.getCustomer()
                , super.getEventTime())), updateShop(ImList.<Server>of(), super.getCustomer()));
        return test;
    }

    public Pair<Optional<Event>, Shop> execute(ImList<Server> servers, Customer customer) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>of(new Serve(super.getCustomer()
                , super.getCustomer().getArrivalTime())), updateShop(servers, customer));
        return test;
    }

    private Shop updateShop(ImList<Server> servers, Customer customer) {
        ImList<Server> currentShop = servers;
        ImList<Server> newShop = ImList.<Server>of();

        int onlyOneServeUpdated = 1;

        for (int i = 0; i < currentShop.size(); i++) {
            Server currentServer = currentShop.get(i);
            if (!currentServer.getIsBusy() && onlyOneServeUpdated == 1) {
                newShop = newShop.add(currentServer.serveNewCustomer(customer));
                onlyOneServeUpdated--;
            } else {
                newShop = newShop.add(currentShop.get(i));
            }
        }

        return new Shop(newShop);
    }

    @Override
    public String toString() {
        return String.format("%s serves by %s", super.toString(), super.getCustomer().getServerId());
    }
}
