package cs2030.simulator;

class Server {
    private final int id;

    Server(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s", this.id);
    }
}
