package cs2030.simulator;

import cs2030.simulator.Customer;

import java.util.Optional;

public class Server {
    private final int id;
    private final boolean isBusy;
    private final int serveCustomerId;
    private final int waitCustomerId;
    private final double nextAvailableTime;

    private static final double SERVICE_TIME = 1.0;

    public Server(int id) { // free
        this.id = id;
        this.isBusy = false;
        this.serveCustomerId = -1;
        this.waitCustomerId = -1;
        this.nextAvailableTime = 0;
    }

    public Server(int id, boolean isBusy, int serveCustomerId, double nextAvailableTime) { // busy
        this.id = id;
        this.isBusy = isBusy;
        this.serveCustomerId = serveCustomerId;
        this.waitCustomerId = -1;
        this.nextAvailableTime = nextAvailableTime;
    }

    public Server(int id, boolean isBusy, int serveCustomerId, int waitCustomerId, double nextAvailableTime) { // busy
        this.id = id;
        this.isBusy = isBusy;
        this.serveCustomerId = serveCustomerId;
        this.waitCustomerId = waitCustomerId;
        this.nextAvailableTime = nextAvailableTime;
    }

    public int getServerId() {
        return this.id;
    }

    public boolean getIsBusy() {
        return this.isBusy;
    }

    public int getServeCustomerId() {
        return this.serveCustomerId;
    }

    public int getWaitCustomerId() {
        return this.waitCustomerId;
    }

    public double getNextAvailableTime() {
        return this.nextAvailableTime;
    }

    public boolean canServe(Customer customer) {
        return !this.getIsBusy() && this.nextAvailableTime <= customer.getArrivalTime();
    }

    public Server serveNewCustomer(Customer customer) {
        return new Server(this.id, true,
                    customer.getCustomerId(), -1, customer.getArrivalTime() + SERVICE_TIME);
    }

    public Server waitNewCustomer(Customer customer) {
        return new Server(this.id, true,
                this.getServeCustomerId(), customer.getCustomerId(), this.getNextAvailableTime());
    }

    @Override
    public String toString() {
        return String.format("%s", this.id);
    }
}
