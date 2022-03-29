package cs2030.util;

import cs2030.simulator.Customer;
import cs2030.simulator.EventState;
import cs2030.simulator.Server;
import cs2030.simulator.Shop;

import cs2030.util.ImList;

public class CustomerUtil {

    public static Customer subsequentFunction(Customer customer, Shop shop) {
        ImList<Server> currentServers = shop.getImServers();
        for (int i = 0; i < currentServers.size(); i++) {
            Server currentServer = currentServers.get(i);
            if (shop.checkAllImServersBusy()) {
                // either wait or leave
                if (currentServer.getWaitCustomerId() == -1){
                    // returning wait state
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.WAIT, currentServers.get(i).getServerId());
                }
                else if (i + 1 == currentServers.size()) {
                    // returning leave state
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.LEAVE, customer.getServerId());
                }
            } else {
                if (currentServer.canServe(customer)) {
                    // returning serve state
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.SERVE, currentServers.get(i).getServerId());
                }
            }
        }

        return customer;
    }
    public static Customer subsequentDoneFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                EventState.DONE, customer.getServerId());
        return newCustomer;
    }

    public static Customer subsequentDefaultFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                EventState.DEFAULT, customer.getServerId());
        return newCustomer;
    }

    public static Customer subsequentServeFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                EventState.SERVE, customer.getServerId());
        return newCustomer;
    }
}
