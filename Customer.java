package cs2030.simulator;

import cs2030.simulator.EventState;

public class Customer {
    private final int id;
    private final double arrivalTime;
    private final EventState currentCustomerState;
    private final int serverId;

    public Customer(int id, double arrivalTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.currentCustomerState = EventState.ARRIVE;
        this.serverId = -1;
    }

    public Customer(int id, double arrivalTime, EventState changeState, int serverServedId) {
        this.id = id;
        this.arrivalTime = arrivalTime;
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

    @Override
    public String toString() {
        return String.format("%d", this.id);
    }
}
