package cs2030.simulator;

import cs2030.simulator.Customer;
import cs2030.simulator.Event;
import cs2030.simulator.EventStub;
import cs2030.simulator.Server;
import cs2030.simulator.Shop;

import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.Optional;

public class Serve extends Event {

    public Serve(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Event>, Shop> execute(ImList<Server> servers, Customer customer,
                                               double serviceTime) {
        return Pair.of(Optional.<Event>of(new Serve(super.getCustomer()
                , super.getCustomer().getArrivalTime())), updateShop(servers, customer, serviceTime));
    }

    private Shop updateShop(ImList<Server> servers, Customer customer,
                            double serviceTime) {
        ImList<Server> currentShop = servers;
        ImList<Server> newShop = ImList.<Server>of();

        int onlyOneServeUpdated = 1;

        int serverID = customer.getServerId();

        if (serverID != -1) {
            Server currentServer = currentShop.get(serverID - 1);
            currentServer = currentServer.serveNewCustomer(customer, serviceTime);
            for (int i = 0; i < currentShop.size(); i++) {
                if (i == serverID - 1) {
                    newShop = newShop.add(currentServer);
                } else {
                    newShop = newShop.add(currentShop.get(i));
                }
            }
        } else {
            for (int i = 0; i < currentShop.size(); i++) {
                Server currentServer = currentShop.get(i);
                if (!currentServer.getIsBusyServing() && onlyOneServeUpdated == 1) {
                    newShop = newShop.add(currentServer.serveNewCustomer(customer, serviceTime));
                    onlyOneServeUpdated--;
                } else {
                    newShop = newShop.add(currentServer);
                }
            }
        }

        return new Shop(newShop);
    }

    @Override
    public String toString() {
        return String.format("%s serves by %s", super.toString(), super.getCustomer().getServerId());
    }
}
