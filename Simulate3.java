package cs2030.simulator;

//import cs2030.util.PQ;
//import cs2030.util.ImList;

import java.util.List;

public class Simulate3 {
    private final ImList<Server> servers;
    private final int numOfServers;
    private final List<Double> custArrivalTimes;
    private final PQ<EventStub> eventStubPQ;

    public Simulate3(int numOfServers, List<Double> custArrivalTimes) {
        this.numOfServers = numOfServers;
        this.custArrivalTimes = custArrivalTimes;
        this.servers = getServers(numOfServers);
        this.eventStubPQ = getEventStubPQ(custArrivalTimes);
    }

    private ImList<Server> getServers(int numOfServers) {
        ImList<Server> currentServers = ImList.<Server>of();
        for (int i = 1; i < numOfServers + 1; i++) {
            currentServers = currentServers.add(new Server(i));
        }
        return currentServers;
    }

    private PQ<EventStub> getEventStubPQ(List<Double> custArrivalTimes) {
        PQ<EventStub> currentEventStubs = new PQ<EventStub>(new EventComparator());
        for (int i = 0; i < custArrivalTimes.size(); i++) {
            double time = custArrivalTimes.get(i);
            currentEventStubs = currentEventStubs.add(new EventStub(new Customer(i + 1, time), time));
        }
        return currentEventStubs;
    }

    public String run() {
        PQ<EventStub> test = this.eventStubPQ;
        String output = "";
        int currentNumOfServersUsed = 0;
        while (!test.isEmpty()) {
//            output += test.poll().first() + "\n";
            EventStub es = test.poll().first();
            Customer customer = es.getCustomer();
            Arrive arrive = new Arrive(customer, es.getEventTime());
            output += arrive.toString() + "\n";
            test = test.poll().second();
        }
        return output + "-- End of Simulation --";
    }

    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s", this.eventStubPQ.toString(), this.servers.toString());
    }
}
