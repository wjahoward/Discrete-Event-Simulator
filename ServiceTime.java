package cs2030.simulator;

public class ServiceTime {
    private final double currentTime;
    private final boolean hasBeenUsed;

    public ServiceTime() {
        this.currentTime = 0.0;
        this.hasBeenUsed = false;
    }

    public ServiceTime(double currentTime) {
        this.currentTime = currentTime;
        this.hasBeenUsed = false;
    }

    public ServiceTime(boolean hasBeenUsed) {
        this.currentTime = this.getCurrentTime();
        this.hasBeenUsed = hasBeenUsed;
    }

    private double getCurrentTime() {
        return currentTime;
    }
}
