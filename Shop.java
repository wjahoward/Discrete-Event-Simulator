package cs2030.simulator;

import cs2030.util.ImList;

import java.util.List;

public class Shop {
    private final List<Server> servers;
    private final ImList<Server> imServers;

    public Shop(List<Server> servers) {
        this.servers = servers;
        this.imServers = ImList.<Server>of();
    }

    public Shop(ImList<Server> servers) {
        // dummy variable
        this.servers = List.<Server>of(new Server(1));
        this.imServers = servers;
    }

    @Override
    public String toString() {
        return this.servers.toString();
    }
}
