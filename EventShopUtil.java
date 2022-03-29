package cs2030.util;

import cs2030.simulator.Arrive;
import cs2030.simulator.Customer;
import cs2030.simulator.Done;
import cs2030.simulator.Event;
import cs2030.simulator.EventStub;
import cs2030.simulator.Leave;
import cs2030.simulator.Serve;
import cs2030.simulator.Server;
import cs2030.simulator.Shop;
import cs2030.simulator.Wait;

import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.Optional;

public class EventShopUtil {

    public static Pair<Optional<Event>, Shop> arriveFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Arrive arrive = new Arrive(customer, es.getEventTime());
        return arrive.execute(new Shop(currentServers));
    }

    public static Pair<Optional<Event>, Shop> serveFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Serve serve = new Serve(customer, es.getEventTime());
        return serve.execute(currentServers, customer);
    }

    public static Pair<Optional<Event>, Shop> doneFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Done done = new Done(customer, es.getEventTime());
        return done.execute(new Shop(currentServers), customer, es);
    }

    public static Pair<Optional<Event>, Shop> leaveFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Leave leave = new Leave(customer, es.getEventTime());
        return leave.execute(new Shop(currentServers));
    }

    public static Pair<Optional<Event>, Shop> waitFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Wait wait = new Wait(customer, es.getEventTime());
        return wait.execute(currentServers, customer);
    }
}
