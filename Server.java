package cs2030.simulator;

public class Server {
    private final int id;
    private final boolean isBusy;

    Server(int id) {
        this.id = id;
        this.isBusy = false;
    }

    @Override
    public String toString() {
        return String.format("%s", this.id);
    }
}
