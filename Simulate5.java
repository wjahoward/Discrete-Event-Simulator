package cs2030.simulator;

import cs2030.simulator.Customer;
import cs2030.simulator.Event;
import cs2030.simulator.EventComparator;
import cs2030.simulator.EventState;
import cs2030.simulator.EventStub;
import cs2030.simulator.Server;
import cs2030.simulator.Shop;
import cs2030.simulator.Statistic;

import cs2030.util.CustomerUtil;
import cs2030.util.EventShopUtil;
import cs2030.util.FixedVariablesUtil;
import cs2030.util.ImList;
import cs2030.util.Pair;
import cs2030.util.PQ;

import java.util.function.Supplier;
import java.util.List;
import java.util.Optional;

public class Simulate5 {
    private final ImList<Server> servers;
    private final PQ<EventStub> eventStubPQ;
    private final List<Pair<Double, Supplier<Double>>> list;

    public Simulate5(int numOfServers, List<Pair<Double, Supplier<Double>>> list) {
        this.servers = getServers(numOfServers);
        this.eventStubPQ = getEventStubPQ(list);
        this.list = list;
    }

    private static ImList<Server> getServers(int numOfServers) {
        ImList<Server> currentServers = ImList.<Server>of();
        for (int i = 1; i < numOfServers + 1; i++) {
            currentServers = currentServers.add(new Server(i));
        }
        return currentServers;
    }

    private static PQ<EventStub> getEventStubPQ(List<Pair<Double, Supplier<Double>>> list) {
        PQ<EventStub> currentEventStubs = new PQ<EventStub>(new EventComparator());
        for (int i = 0; i < list.size(); i++) {
            double time = list.get(i).first();
            Customer newCustomer = new Customer(i + 1, time);
            currentEventStubs = currentEventStubs.add(new EventStub(newCustomer, time));
        }
        return currentEventStubs;
    }

    public String run() {
        PQ<EventStub> currentEventStub = this.eventStubPQ;
        ImList<Server> currentServers = this.servers;
        Statistic statistic = new Statistic();
        String output = "";
        // counter
        int j = 0;

        while (!currentEventStub.isEmpty()) {
            EventStub es = currentEventStub.poll().first();
            Customer customerLoop = es.getCustomer();
            EventState eventTypeCustomerLoop = customerLoop.getCurrentCustomerState();

            if (eventTypeCustomerLoop == EventState.ARRIVE) {
                Pair<Optional<Event>, Shop> arriveTest = EventShopUtil.arriveFunction(es, customerLoop, currentServers);
                Optional<Event> arriveFirst = arriveTest.first();
                output += arriveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())) + "\n";
                // whether if is wait, done, served or leave
                Shop shopSecond = arriveTest.second();
                customerLoop = CustomerUtil.subsequentFunction(customerLoop, shopSecond);
            } else if (eventTypeCustomerLoop == EventState.SERVE) {
                double serviceTime = this.list.get(j).second().get();
                Pair<Optional<Event>, Shop> serveEvent = EventShopUtil.serveFunction(es, customerLoop, currentServers, serviceTime);
                Optional<Event> serveFirst = serveEvent.first();
                Event newEventStub = serveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime()));
                output += newEventStub + "\n";
                Shop shopSecond = serveEvent.second();
                // update currentServers to those new servers that are busy
                currentServers = shopSecond.getImServers();
                // change to done event
                customerLoop = CustomerUtil.subsequentDoneFunction(customerLoop);
                // update es event time
                es = new EventStub(newEventStub.getCustomer(), newEventStub.getEventTime() + serviceTime);
                statistic = statistic.addNumOfCustomersServed();
                j++;
            } else if (eventTypeCustomerLoop == EventState.DONE) {
                Pair<Optional<Event>, Shop> doneEvent = EventShopUtil.doneFunction(es, customerLoop, currentServers);
                Optional<Event> doneFirst = doneEvent.first();
                output += doneFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())) + "\n";
                Shop shopSecond = doneEvent.second();
                // update currentServers to those new servers that are not busy
                currentServers = shopSecond.getImServers();
                // change to default event
                customerLoop = CustomerUtil.subsequentDefaultFunction(customerLoop);
            } else if (eventTypeCustomerLoop == EventState.LEAVE) {
                Pair<Optional<Event>, Shop> leaveEvent = EventShopUtil.leaveFunction(es, customerLoop, currentServers);
                Optional<Event> leaveFirst = leaveEvent.first();
                output += leaveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())) + "\n";
                customerLoop = CustomerUtil.subsequentDefaultFunction(customerLoop);
                statistic = statistic.addNumOfCustomersLeftWithoutServed();
            } else if (eventTypeCustomerLoop == EventState.WAIT) {
                double defaultArrivalTime = customerLoop.getArrivalTime();
                Pair<Optional<Event>, Shop> waitEvent = EventShopUtil.waitFunction(es, customerLoop, currentServers);
                Optional<Event> waitFirst = waitEvent.first();
                Event newEventStub = waitFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime()));
                output += newEventStub + "\n";
                Shop shopSecond = waitEvent.second();
                // update currentServers to those new servers that have customers waiting
                currentServers = shopSecond.getImServers();
                // change to serve event
                customerLoop = CustomerUtil.subsequentServeFunction(customerLoop);
                // update es event time
                int customerId = customerLoop.getCustomerId();
                // first element: nextAvailableTime
                // second element: serverServedId
                Pair<Double, Integer> nextAvailableTimeServerServedId = getNextAvailableTimeServerServedId(currentServers, customerId);
                customerLoop = new Customer(customerId,
                        nextAvailableTimeServerServedId.first(), customerLoop.getCurrentCustomerState(),
                        nextAvailableTimeServerServedId.second());
                es = new EventStub(customerLoop, nextAvailableTimeServerServedId.first());
                statistic = statistic.addWaitingTime(nextAvailableTimeServerServedId.first() -
                        defaultArrivalTime);
            }

            currentEventStub = currentEventStub.poll().second();
            if (customerLoop.getCurrentCustomerState() != EventState.DEFAULT) {
                currentEventStub = currentEventStub.add(new EventStub(customerLoop, es.getEventTime()));
            }
        }
        return output + statistic.toString();
    }

    private Pair<Double, Integer> getNextAvailableTimeServerServedId(ImList<Server> servers, int customerId) {
        for (int i = 0; i < servers.size(); i++) {
            Server currentServer = servers.get(i);
            if (currentServer.getWaitCustomerId() == customerId) {
                return Pair.of(currentServer.getNextAvailableTime(),
                        currentServer.getServerId());
            }
        }

        return Pair.of(-1.0, -1);
    }

    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s", this.eventStubPQ.toString(), this.servers.toString());
    }
}
