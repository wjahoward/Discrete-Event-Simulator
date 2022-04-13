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
import cs2030.util.ImList;
import cs2030.util.Pair;
import cs2030.util.PQ;

import java.util.function.Supplier;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class Simulate7 {
    private final ImList<Server> servers;
    private final PQ<EventStub> eventStubPQ;
    private final List<Pair<Double, Supplier<Double>>> list;
    private final int qmax;
    private final Supplier<Double> restTime;

    public Simulate7(int numOfServers, List<Pair<Double, Supplier<Double>>> list, int qmax, Supplier<Double> restTime) {
        this.servers = getServers(numOfServers, qmax);
        this.eventStubPQ = getEventStubPQ(list);
        this.list = list;
        this.qmax = qmax;
        this.restTime = restTime;
    }

    private static ImList<Server> getServers(int numOfServers, int qmax) {
        ImList<Server> currentServers = ImList.<Server>of();
        for (int i = 1; i < numOfServers + 1; i++) {
            currentServers = currentServers.add(new Server(i, qmax));
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
        double currentRestTime = 0.0;
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
                Event done = doneFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime()));
                output += done + "\n";
                int serverNumberResting = 0;
                int customerLoopServedId = customerLoop.getServerId();
                Shop shopSecond = doneEvent.second();
                // update currentServers to those new servers that are not busy
                currentServers = shopSecond.getImServers();

                for (int i = 0; i < currentServers.size(); i++) {
                    Server server = currentServers.get(i);;
                    if (server.getServerId() == customerLoopServedId) {
                        currentRestTime = this.restTime.get();
                        if (currentRestTime != 0.0) {
                            Customer noCustomerServed = new Customer(-1, -1, EventState.SERVER_REST);
                            EventStub serverRestEvent = new EventStub(noCustomerServed, -1);
                            if (server.getNextAvailableTime() == 0.0) {
                                serverRestEvent = new EventStub(noCustomerServed, done.getEventTime() + currentRestTime);
                            } else {
                                serverRestEvent = new EventStub(noCustomerServed, server.getNextAvailableTime() + currentRestTime);
                            }
                            currentEventStub = currentEventStub.add(serverRestEvent);
                            serverNumberResting = server.getServerId();

                            // update server that is resting
                            if (serverNumberResting != 0) {
                                ImList<Server> newServers = ImList.<Server>of();
                                for (int a = 0; a < currentServers.size(); a++) {
                                    if (currentServers.get(a).getServerId() == serverNumberResting) {
                                        Server newServer = new Server(currentServers.get(a), serverRestEvent.getEventTime(), true);
                                        newServers = newServers.add(newServer);
                                    } else {
                                        newServers = newServers.add(currentServers.get(a));
                                    }
                                }

                                    currentServers = newServers;
                                }
                            } else {
                                if (server.getWaitingCapacity() != 0) {
                                    Customer updatedCustomer = new Customer(server.getNextWaitingCustomer(),
                                        server.getNextAvailableTime(), EventState.SERVE);
                                    EventStub serverDone = new EventStub(updatedCustomer, server.getNextAvailableTime());
                                    currentEventStub = currentEventStub.add(serverDone);
                                    statistic = statistic.addWaitingTime(updatedCustomer.getArrivalTime() -
                                        updatedCustomer.getOriginalArrivalTime());
                            }
                        }
                    }
                }

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
                Pair<Optional<Event>, Shop> waitEvent = EventShopUtil.waitFunction(es, customerLoop, currentServers);
                Optional<Event> waitFirst = waitEvent.first();
                Event newEventStub = waitFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime()));
                output += newEventStub + "\n";
                Shop shopSecond = waitEvent.second();
                // update currentServers to those new servers that have customers waiting
                currentServers = shopSecond.getImServers();
                customerLoop = CustomerUtil.subsequentDefaultFunction(customerLoop);
            } else if (eventTypeCustomerLoop == EventState.SERVER_REST) {
                // TODO:
                //  1. set isResting false
                //  2. check if there's any waiting server
                //  3. go back to served
                ImList<Server> newServers = ImList.<Server>of();
                for (int i = 0; i < currentServers.size(); i++) {
                    Server currentServer = currentServers.get(i);
                    if (currentServer.getIsResting() && currentServer.getNextAvailableTime() == es.getEventTime()) {
                        currentServer = new Server(currentServer, 0.0, false);
                        newServers = newServers.add(currentServer);
                        if (currentServer.getWaitingCapacity() != 0) { // last customer
                            Customer updatedCustomer = new Customer(currentServer.getNextWaitingCustomer(),
                                    es.getEventTime(), EventState.SERVE);
                            EventStub serverDone = new EventStub(updatedCustomer, currentServer.getNextAvailableTime());
                            customerLoop = updatedCustomer;
                            currentEventStub = currentEventStub.add(serverDone);
                            statistic = statistic.addWaitingTime(updatedCustomer.getArrivalTime() -
                                    updatedCustomer.getOriginalArrivalTime());
                        }
                    } else {
                        newServers = newServers.add(currentServer);
                    }
                }
                currentServers = newServers;
            }

            currentEventStub = currentEventStub.poll().second();

            if (customerLoop.getCurrentCustomerState() != EventState.DEFAULT &&
                customerLoop.getCurrentCustomerState() != EventState.SERVER_REST) {
                currentEventStub = currentEventStub.add(new EventStub(customerLoop, es.getEventTime()));
            }
        }
        return output + statistic.toString();
    }

    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s", this.eventStubPQ.toString(), this.servers.toString());
    }
}
