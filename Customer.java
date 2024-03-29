package cs2030.simulator;

import cs2030.simulator.EventState;

public class Customer {
    private final int id;
    private final double arrivalTime;
    private final double originalArrivalTime;
    private final EventState currentCustomerState;
    private final int serverId;

    public Customer(int id, double arrivalTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = arrivalTime;
        this.currentCustomerState = EventState.ARRIVE;
        this.serverId = -1;
    }

    public Customer(int id, double arrivalTime, EventState changeState) { // server resting
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = arrivalTime;
        this.currentCustomerState = changeState;
        this.serverId = -1;
    }

    public Customer(Customer customer, double arrivalTime, EventState changeState) {
        this.id = customer.id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = customer.originalArrivalTime;
        this.currentCustomerState = changeState;
        this.serverId = customer.serverId;
    }

    public Customer(Customer customer, EventState changeState) {
        this.id = customer.id;
        this.arrivalTime = customer.arrivalTime;
        this.originalArrivalTime = customer.originalArrivalTime;
        this.currentCustomerState = changeState;
        this.serverId = customer.serverId;
    }

    public Customer(int id, double arrivalTime, EventState changeState, int serverServedId) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = arrivalTime;
        this.currentCustomerState = changeState;
        this.serverId = serverServedId;
    }

    public Customer(int id, double arrivalTime, EventState changeState,
                    double originalArrivalTime, int serverServedId) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.originalArrivalTime = originalArrivalTime;
        this.currentCustomerState = changeState;
        this.serverId = serverServedId;
    }

    public int getCustomerId() {
        return this.id;
    }

    public double getArrivalTime() {
        return this.arrivalTime;
    }

    public EventState getCurrentCustomerState() { return this.currentCustomerState; }

    public int getServerId() {
        return this.serverId;
    }

    public double getOriginalArrivalTime() { return this.originalArrivalTime; }

    @Override
    public String toString() {
        return String.format("%d", this.id);
    }
}
