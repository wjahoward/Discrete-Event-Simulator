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
    private final int qmax;
    // TODO: include isResting property
    private final boolean isResting;

    public Server(int id) {
        this(id, false, -1, getNewSetOfWaitingCustomers(1), 0, 1, false);
    }

    public Server(int id, int qmax) { // free
        this(id, false, -1, getNewSetOfWaitingCustomers(qmax), 0, qmax,false);
    }

//    public Server(int id, boolean isBusyServing, int serveCustomerId, double nextAvailableTime) { // busy without waiting - for Simulator 5
//        this(id, isBusyServing, serveCustomerId, ImList.<Customer>of(), nextAvailableTime, 1);
//    }

    public Server(Server server, ImList<Customer> waitingCustomerUpdate) {
        this(server.id, server.isBusyServing, server.serveCustomerId, waitingCustomerUpdate, server.nextAvailableTime, 1, false);
    }

    public Server(Server server, boolean isResting) {
        this(server.id, server.isBusyServing, server.serveCustomerId, server.waitingCustomers, server.nextAvailableTime, server.qmax, isResting);
    }

    public Server(int id, boolean isBusyServing, int serveCustomerId, ImList<Customer> waitingCustomers,
                  double nextAvailableTime, int qmax, boolean isResting) {
        this.id = id;
        this.isBusyServing = isBusyServing;
        this.serveCustomerId = serveCustomerId;
        this.waitingCustomers = waitingCustomers;
        this.isMaxWaiting = checkAllWaitingCustomers(waitingCustomers);
        this.nextAvailableTime = nextAvailableTime;
        this.qmax = qmax;
        this.isResting = isResting;
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

    public int getQmax() { return this.qmax; }

    public boolean getIsResting() { return this.isResting; }

    public boolean canServe(Customer customer) {
        return !this.getIsBusyServing() && this.nextAvailableTime <= customer.getArrivalTime();
    }

    public Server serveNewCustomer(Customer customer, double serviceTime) {
        return new Server(customer.getServerId(), true,
                customer.getCustomerId(), this.removeAnyWaitingCustomer(customer),
                customer.getArrivalTime() + serviceTime, this.qmax, this.isResting);
    }

    private ImList<Customer> removeAnyWaitingCustomer(Customer customer) {
        ImList<Customer> newListRemovingWaitingCustomer = ImList.<Customer>of();
        for (int i = 0; i < this.getWaitingCustomers().size(); i++) {
            Customer waitingCustomer = this.getWaitingCustomers().get(i);
            if (waitingCustomer.getCustomerId() != -1 &&
                    waitingCustomer.getCustomerId() != customer.getCustomerId()) {
                newListRemovingWaitingCustomer = newListRemovingWaitingCustomer.add(waitingCustomer);
            }
        }

        if (newListRemovingWaitingCustomer.size() == 0) {
            return getNewSetOfWaitingCustomers(this.qmax);
        }

        return newListRemovingWaitingCustomer;
    }

    public int getWaitingCapacity() {
        int counter = 0;
        for (int i = 0; i < this.waitingCustomers.size(); i++) {
            if (this.waitingCustomers.get(i).getCustomerId() != -1) {
                counter++;
            }
        }
        return counter;
    }

    public Customer getNextWaitingCustomer() {
        return this.waitingCustomers.get(0);
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
