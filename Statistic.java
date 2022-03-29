package cs2030.simulator;

public class Statistic {
    private final double waitingTime;
    private final int numOfCustomersServed;
    private final int numOfCustomersLeftWithoutServed;

    public Statistic() {
        this.waitingTime = 0.0;
        this.numOfCustomersServed = 0;
        this.numOfCustomersLeftWithoutServed = 0;
    }

    public Statistic(double waitingTime, int numOfCustomersServed, int numOfCustomersLeftWithoutServed) {
        this.waitingTime = waitingTime;
        this.numOfCustomersServed = numOfCustomersServed;
        this.numOfCustomersLeftWithoutServed = numOfCustomersLeftWithoutServed;
    }

    private double getWaitingTime() {
        return this.waitingTime;
    }

    private int getNumOfCustomersServed() {
        return this.numOfCustomersServed;
    }

    private int getNumOfCustomersLeftWithoutServed() {
        return this.numOfCustomersLeftWithoutServed;
    }

    public Statistic addWaitingTime(double updatedWaitingTime) {
        return new Statistic(this.waitingTime + updatedWaitingTime,
                    this.getNumOfCustomersServed(), this.getNumOfCustomersLeftWithoutServed());
    }

    public Statistic addNumOfCustomersServed() {
        return new Statistic(this.getWaitingTime(),
                this.numOfCustomersServed + 1, this.getNumOfCustomersLeftWithoutServed());
    }

    public Statistic addNumOfCustomersLeftWithoutServed() {
        return new Statistic(this.getWaitingTime(),
                this.getNumOfCustomersServed(),
                this.getNumOfCustomersLeftWithoutServed() + 1);
    }

    @Override
    public String toString() {
        return String.format("[%.3f %s %s]", this.waitingTime / this.numOfCustomersServed,
                    this.numOfCustomersServed, this.numOfCustomersLeftWithoutServed);
    }
}
