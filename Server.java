package cs2030.simulator;

import cs2030.simulator.Customer;

import cs2030.util.Pair;

import cs2030.util.ImList;

public class Server {
    private final int id;
    private final boolean isBusyServing;
    private final int serveCustomerId;
    private final ImList<Customer> waitingCustomers;
    private final boolean isMaxWaiting;
    private final double nextAvailableTime;

    public Server(int id) {
        this(id, false, -1, getNewSetOfWaitingCustomers(1), 0);
    }

    public Server(int id, int qmax) { // free
        this(id, false, -1, getNewSetOfWaitingCustomers(qmax), 0);
    }

    public Server(int id, boolean isBusyServing, int serveCustomerId, double nextAvailableTime) { // busy without waiting - for Simulator 5
        this(id, isBusyServing, serveCustomerId, ImList.<Customer>of(), nextAvailableTime);
    }

    public Server(Server server, ImList<Customer> waitingCustomerUpdate) {
        this(server.id, server.isBusyServing, server.serveCustomerId, waitingCustomerUpdate, server.nextAvailableTime);
    }

    public Server(int id, boolean isBusyServing, int serveCustomerId, ImList<Customer> waitingCustomers, double nextAvailableTime) {
        this.id = id;
        this.isBusyServing = isBusyServing;
        this.serveCustomerId = serveCustomerId;
        this.waitingCustomers = waitingCustomers;
        this.isMaxWaiting = checkAllWaitingCustomers(waitingCustomers);
        this.nextAvailableTime = nextAvailableTime;
    }

    private static ImList<Customer> getNewSetOfWaitingCustomers(int qmax) {
        ImList<Customer> newWaitingCustomer = ImList.<Customer>of();
        for (int i = 0; i < qmax; i++) {
            Customer defaultCustomer = new Customer(-1, 0);
            newWaitingCustomer = newWaitingCustomer.add(defaultCustomer);
        }

        return newWaitingCustomer;
    }

    private static boolean checkAllWaitingCustomers(ImList<Customer> currentCustomers) {
        for (int i = 0; i < currentCustomers.size(); i++) {
            if (currentCustomers.get(i).getCustomerId() == -1) { // default waiting customer
                return false;
            }
        }

        return true;
    }

    public int getServerId() {
        return this.id;
    }

    public boolean getIsBusyServing() {
        return this.isBusyServing;
    }

    public int getServeCustomerId() {
        return this.serveCustomerId;
    }

    public ImList<Customer> getWaitingCustomers() {
        return this.waitingCustomers;
    }

    public boolean getIsMaxWaiting() {
        return this.isMaxWaiting;
    }

    public double getNextAvailableTime() {
        return this.nextAvailableTime;
    }

    public boolean canServe(Customer customer) {
        return !this.getIsBusyServing() && this.nextAvailableTime <= customer.getArrivalTime();
    }

    public Server serveNewCustomer(Customer customer, double serviceTime) {
        return new Server(this.id, true,
                customer.getCustomerId(), this.removeAnyWaitingCustomer(customer),
                customer.getArrivalTime() + serviceTime);
    }

    private ImList<Customer> removeAnyWaitingCustomer(Customer customer) {
        ImList<Customer> newListRemovingWaitingCustomer = ImList.<Customer>of();
        int initialSize = this.getWaitingCustomers().size();
        for (int i = 0; i < initialSize; i++) {
            Customer waitingCustomer = this.getWaitingCustomers().get(i);
            if (waitingCustomer.getCustomerId() != -1 &&
                    waitingCustomer.getCustomerId() != customer.getCustomerId()) {
                newListRemovingWaitingCustomer = newListRemovingWaitingCustomer.add(waitingCustomer);
            }
        }

        System.out.println("\nremoving");
        System.out.println(newListRemovingWaitingCustomer.size());

        if (newListRemovingWaitingCustomer.size() == 0) {
            return getNewSetOfWaitingCustomers(initialSize);
        } else if (newListRemovingWaitingCustomer.size() != this.getWaitingCustomers().size()) {
            newListRemovingWaitingCustomer = newListRemovingWaitingCustomer.add(new Customer(-1, 0));
        }

        System.out.println(newListRemovingWaitingCustomer);

        return newListRemovingWaitingCustomer;
    }

    public Server waitNewCustomer(Customer customer) {
        return new Server(this.id, true,
                this.serveCustomerId, this.addedCustomerToWaitingList(customer),
                this.nextAvailableTime);
    }

    private ImList<Customer> addedCustomerToWaitingList(Customer customer) {
        ImList<Customer> newCustomerWaitingList = ImList.<Customer>of();
        int updateOnce = 1;
        for (int i = 0; i < this.waitingCustomers.size(); i++) {
            Customer currentWaitingCustomer = this.waitingCustomers.get(i);
            if (currentWaitingCustomer.getCustomerId() == -1 && updateOnce == 1) {
                newCustomerWaitingList = newCustomerWaitingList.add(customer);
                updateOnce--;
            } else {
                newCustomerWaitingList = newCustomerWaitingList.add(currentWaitingCustomer);
            }
        }

        return newCustomerWaitingList;
    }

    @Override
    public String toString() {
        return String.format("%s", this.id);
    }
}
