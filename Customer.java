package cs2030.simulator;

public class Customer {
    private final int id;
    private final double arrivalTime;

    Customer(int id, double arrivalTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.format("%d", this.id);
    }
}
