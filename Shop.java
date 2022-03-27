package cs2030.simulator;

import cs2030.util.ImList;

import java.util.List;

public class Shop {
    private final List<Server> servers;
    private final ImList<Server> imServers;

    public Shop(List<Server> servers) {
        this.servers = servers;
        this.imServers = getServers(servers);
    }

    private static ImList<Server> getServers(List<Server> servers) {
        ImList<Server> test = ImList.<Server>of();
        for (int i = 0; i < servers.size(); i++) {
            test = test.add(servers.get(i));
        }
        return test;
    }

    public Shop(ImList<Server> servers) {
        // dummy variable
        this.servers = List.<Server>of(new Server(1));
        this.imServers = servers;
    }

    @Override
    public String toString() {
        return this.imServers.toString();
    }
}
