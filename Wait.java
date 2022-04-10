package cs2030.simulator;

import cs2030.simulator.Customer;
import cs2030.simulator.Event;
import cs2030.simulator.Server;
import cs2030.simulator.Shop;

import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.Optional;

public class Wait extends Event {

    public Wait(Customer customer, double eventTime) {
        super(customer, eventTime);
    }

    public Pair<Optional<Event>, Shop> execute(ImList<Server> servers, Customer customer) {
        Pair<Optional<Event>, Shop> test = Pair.of(Optional.<Event>of(new Wait(super.getCustomer()
                , super.getCustomer().getArrivalTime())), updateShop(servers, customer));
        return test;
    }

    private Shop updateShop(ImList<Server> servers, Customer customer) {
        ImList<Server> currentShop = servers;
        ImList<Server> newShop = ImList.<Server>of();
        ImList<Customer> updatedCustomerList = ImList.<Customer>of();

        int onlyOneWaitUpdated = 1;
        for (int i = 0; i < currentShop.size(); i++) {
            Server currentServer = currentShop.get(i);
            if (currentServer.getIsMaxWaiting()) {
                newShop = newShop.add(currentServer);
            } else {
                System.out.println("\n" + currentServer.getWaitingCustomers());
                for (int j = 0; j < currentServer.getWaitingCustomers().size(); j++) {
                    Customer currentWaitingCustomer = currentServer.getWaitingCustomers().get(j);
                    int currentWaitCustomerId = currentWaitingCustomer.getCustomerId();
                    if (currentWaitCustomerId == -1 && onlyOneWaitUpdated == 1) {
//                    newShop = newShop.add(currentServer.waitNewCustomer(customer));
                        updatedCustomerList = updatedCustomerList.add(customer);
                        onlyOneWaitUpdated--;
                    } else {
                        updatedCustomerList = updatedCustomerList.add(currentWaitingCustomer);
                    }
                }
                System.out.println(customer.getCustomerId());
                System.out.println(updatedCustomerList);
                currentServer = new Server(currentServer, updatedCustomerList);
                updatedCustomerList = ImList.<Customer>of();
                newShop = newShop.add(currentServer);
            }
        }
        return new Shop(newShop);
    }

    @Override
    public String toString() {
        return String.format("%s waits at %s", super.toString(), super.getCustomer().getServerId());
    }
}