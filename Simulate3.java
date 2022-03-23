package cs2030.simulator;

import cs2030.util.PQ;
import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.List;
import java.util.Optional;

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
        ImList<Server> currentServers = servers;

        // dummy variables
        Customer customer = new Customer(1, 1);
        double eventTime = 1;

        Event currentEvent = new EventStub(customer, eventTime);

        while (!test.isEmpty()) {
            // arrive
            Pair<Optional<Event>, Shop> arriveTest = arriveFunction(test);
            System.out.println(arriveTest);
            output += arriveFunction(test) + "\n";

            // serve
//            for (int i = 0; i < currentServers.size(); i++) {
//                if (currentServers.get(i).isBusy) {
//                    if (i + 1 == currentServers.size()) {
//                        output += leaveFunction(test) + "\n";
//                    }
//                    continue;
//                } else {
//
//                }
//            }

            test = test.poll().second();
        }
        return output + "-- End of Simulation --";
    }

    private Pair<Optional<Event>, Shop> arriveFunction(PQ<EventStub> test) {
        EventStub es = test.poll().first();
        Customer customer = es.getCustomer();
        Arrive arrive = new Arrive(customer, es.getEventTime());
//        return arrive.toString();
        return arrive.execute(new Shop(this.servers));
    }

    private String leaveFunction(PQ<EventStub> test) {
        EventStub es = test.poll().first();
        Customer customer = es.getCustomer();
        Leave arrive = new Leave(customer, es.getEventTime());
        return arrive.toString();
    }

    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s", this.eventStubPQ.toString(), this.servers.toString());
    }
}
