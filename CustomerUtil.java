package cs2030.util;

import cs2030.simulator.Customer;
import cs2030.simulator.EventState;
import cs2030.simulator.Server;
import cs2030.simulator.Shop;

import cs2030.util.ImList;

public class CustomerUtil {

    public static Customer subsequentFunction(Customer customer, Shop shop) {;
        ImList<Server> currentServers = shop.getImServers();
        for (int i = 0; i < currentServers.size(); i++) {
            Server currentServer = currentServers.get(i);
            // check if both servers are resting
            // if yes, check if currentServer maxCustomersWaiting is max
            // if yes (upon checking for both servers), this customer will leave
            if (shop.checkServersResting()) {
                if (!currentServer.getIsMaxWaiting()) {
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.WAIT, currentServers.get(i).getServerId());
                } else if (i + 1 == currentServers.size() ){
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.LEAVE, customer.getServerId());
                }
            } else if (shop.checkAllImServersBusy()) {
                // either wait or leave
                if (!shop.checkAllImServersWaiting()) {
                    if (!currentServer.getIsMaxWaiting()) {
                        // returning wait state
                        return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                                EventState.WAIT, currentServers.get(i).getServerId());
                    }
                }

                if (shop.checkAllImServersWaiting() && shop.checkAllImServersBusy()) {
                    // returning leave state
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.LEAVE, customer.getServerId());
                }
            } else {
                if (currentServer.canServe(customer) && !currentServer.getIsResting()) {
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
                EventState.DONE, customer.getOriginalArrivalTime(), customer.getServerId());
        return newCustomer;
    }

    public static Customer subsequentDefaultFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                EventState.DEFAULT, customer.getOriginalArrivalTime(), customer.getServerId());
        return newCustomer;
    }

    public static Customer subsequentServeFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                EventState.SERVE, customer.getServerId());
        return newCustomer;
    }
}
