package cs2030.simulator;

import java.util.List;

public class Shop {
    private final List<Server> servers;

    Shop(List<Server> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return this.servers.toString();
    }
}
