package cs2030.simulator;

import cs2030.simulator.Customer;
import cs2030.simulator.Event;
import cs2030.simulator.EventStub;
import cs2030.simulator.Server;
import cs2030.simulator.Shop;

import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.Optional;

public class Done extends Event {

    public Done(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        return Pair.of(Optional.<Event>of(new Done(super.getCustomer()
                , super.getEventTime())), shop);
    }

    public Pair<Optional<Event>, Shop> execute(Shop shop, Customer customer, EventStub es) {
        return Pair.of(Optional.<Event>of(new Done(super.getCustomer()
                , es.getEventTime())), updateShop(shop, customer));
    }

    private Shop updateShop(Shop shop, Customer customer) {
        ImList<Server> currentShop = shop.getImServers();
        ImList<Server> newShop = ImList.<Server>of();

        final boolean isBusy = false;
        int serverIdServeCustomer = customer.getServerId();

        for (int i = 0; i < currentShop.size(); i++) {
            Server currentServer = currentShop.get(i);
            if (currentServer.getIsBusyServing() && currentServer.getServerId() == serverIdServeCustomer) {
                double nextAvailableTime = currentServer.getWaitingCustomers().size() == 0 ? 0 : currentServer.getNextAvailableTime();
                newShop = newShop.add(new Server(currentServer.getServerId(), isBusy, -1,
                            currentServer.getWaitingCustomers(), nextAvailableTime, currentServer.getQmax(),
                            currentServer.getIsResting()));
            } else {
                newShop = newShop.add(currentServer);
            }
        }

        return new Shop(newShop);
    }

    @Override
    public String toString() {
        return String.format("%s done serving by %s", super.toString(), super.getCustomer().getServerId());
    }
}
