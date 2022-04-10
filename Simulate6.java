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

public class Simulate6 {
    private final ImList<Server> servers;
    private final PQ<EventStub> eventStubPQ;
    private final List<Pair<Double, Supplier<Double>>> list;
    private final int qmax;

    public Simulate6(int numOfServers, List<Pair<Double, Supplier<Double>>> list, int qmax) {
        this.servers = getServers(numOfServers, qmax);
        this.eventStubPQ = getEventStubPQ(list);
        this.list = list;
        this.qmax = qmax;
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
        double serviceTimeCached = 0.0;
        // counter
        int j = 0;

        while (!currentEventStub.isEmpty()) {
            EventStub es = currentEventStub.poll().first();
            Customer customerLoop = es.getCustomer();
            EventState eventTypeCustomerLoop = customerLoop.getCurrentCustomerState();

            if (es.getEventTime() == 999) { // service time that is invalid
                break;
            }

            if (eventTypeCustomerLoop == EventState.ARRIVE) {
                Pair<Optional<Event>, Shop> arriveTest = EventShopUtil.arriveFunction(es, customerLoop, currentServers);
                Optional<Event> arriveFirst = arriveTest.first();
                output += arriveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())) + "\n";
                System.out.println(arriveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())));
                // whether if is wait, done, served or leave
                Shop shopSecond = arriveTest.second();
                customerLoop = CustomerUtil.subsequentFunction(customerLoop, shopSecond);
            } else if (eventTypeCustomerLoop == EventState.SERVE) {
                double serviceTime = this.list.get(j).second().get();
                System.out.println("\nservice time: " + serviceTime);
                Pair<Optional<Event>, Shop> serveEvent = EventShopUtil.serveFunction(es, customerLoop, currentServers, serviceTime);
                Optional<Event> serveFirst = serveEvent.first();
                Event newEventStub = serveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime()));
                output += newEventStub + "\n";
                System.out.println(newEventStub);
                Shop shopSecond = serveEvent.second();
                // update currentServers to those new servers that are busy
                currentServers = shopSecond.getImServers();
                System.out.println("\ncome on kur " + currentServers.get(0).getWaitingCustomers());
                // change to done event
                customerLoop = CustomerUtil.subsequentDoneFunction(customerLoop);
                // get those waiting customers that have same eventTime as current event stub
                // and change accordingly
                ImList<Server> newServer = ImList.<Server>of(); // removing the waiting customer since is changed to serve
                for (int a = 0; a < currentServers.size(); a++) {
                    Server currentServer = currentServers.get(a);
                    if (currentServer.getWaitingCustomers().size() > 1) {
                        for (int b = 0; b < currentServer.getWaitingCustomers().size(); b++) {
                            Customer currentWaitCustomer = currentServer.getWaitingCustomers().get(b);
                            if (b == 0 && currentWaitCustomer.getCustomerId() != -1) {
                                System.out.println("\ncustid" + currentWaitCustomer.getCustomerId());
                                double updatedTime = es.getEventTime() + serviceTime;
                                Customer updatedCustomer = new Customer(currentWaitCustomer, updatedTime, EventState.SERVE);
                                EventStub test = new EventStub(updatedCustomer, updatedTime);
                                // remove waiting customer
                                for (int c = 0; c < currentServers.size(); c++) {
                                    if (currentServers.get(c).equals(currentServer)) {
                                        newServer = newServer.add(currentServers.get(c).serveNewCustomer(updatedCustomer, updatedTime));
                                    } else {
                                        newServer = newServer.add(currentServers.get(c));
                                    }
                                }
                                currentEventStub = currentEventStub.add(test);
                                statistic = statistic.addWaitingTime(serviceTime);
                            }
                        }
                    }
                }

                if (!newServer.equals(ImList.<Customer>of())) {
                    currentServers = newServer;
                }

                // update es event time
                es = new EventStub(newEventStub.getCustomer(), newEventStub.getEventTime() + serviceTime);
                statistic = statistic.addNumOfCustomersServed();
                j++;
            } else if (eventTypeCustomerLoop == EventState.DONE) {
                Pair<Optional<Event>, Shop> doneEvent = EventShopUtil.doneFunction(es, customerLoop, currentServers);
                Optional<Event> doneFirst = doneEvent.first();
                output += doneFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())) + "\n";
                System.out.println(doneFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())));
                Shop shopSecond = doneEvent.second();
                // update currentServers to those new servers that are not busy
                currentServers = shopSecond.getImServers();
                // change to default event
                customerLoop = CustomerUtil.subsequentDefaultFunction(customerLoop);
                System.out.println("\ndone");
                System.out.println(customerLoop.getCustomerId());
                int customerLoopId = customerLoop.getCustomerId();
            } else if (eventTypeCustomerLoop == EventState.LEAVE) {
                Pair<Optional<Event>, Shop> leaveEvent = EventShopUtil.leaveFunction(es, customerLoop, currentServers);
                Optional<Event> leaveFirst = leaveEvent.first();
                output += leaveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())) + "\n";
                System.out.println(leaveFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime())));
                customerLoop = CustomerUtil.subsequentDefaultFunction(customerLoop);
                statistic = statistic.addNumOfCustomersLeftWithoutServed();
            } else if (eventTypeCustomerLoop == EventState.WAIT) {
                double defaultArrivalTime = customerLoop.getArrivalTime();
                Pair<Optional<Event>, Shop> waitEvent = EventShopUtil.waitFunction(es, customerLoop, currentServers);
                Optional<Event> waitFirst = waitEvent.first();
                Event newEventStub = waitFirst.orElse(new Event(customerLoop,
                        customerLoop.getArrivalTime()));
                output += newEventStub + "\n";
                System.out.println(newEventStub);
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
                // check if this customer is part of the waiting list and check if first slot have been used
                // if yes, set the waiting customer(s) es.getEventTime as 999
                for (int a = 0; a < currentServers.size(); a++) {
                    Server currentServer = currentServers.get(a);
                    for (int b = 0; b < currentServer.getWaitingCustomers().size(); b++) {
                        if (b > 0 && currentServer.getWaitingCustomers().get(b).getCustomerId() != -1) {
                            es = new EventStub(customerLoop, 999);
                        }
                    }
                }
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
            for (int j = 0; j < currentServer.getWaitingCustomers().size(); j++) {
                int currentWaitCustomerId = currentServer.getWaitingCustomers().get(j).getCustomerId();
                if (currentWaitCustomerId == customerId) {
                    return Pair.of(currentServer.getNextAvailableTime(),
                            currentServer.getServerId());
                }
            }
        }

        return Pair.of(-1.0, -1);
    }

    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s", this.eventStubPQ.toString(), this.servers.toString());
    }
}
