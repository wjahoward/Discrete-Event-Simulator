package cs2030.simulator;

import cs2030.simulator.Server;
import cs2030.simulator.EventStub;
import cs2030.simulator.Customer;
import cs2030.simulator.EventComparator;

import cs2030.util.PQ;
import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.List;
import java.util.Optional;

public class Simulate3 {
    private final ImList<Server> servers;
    private final PQ<EventStub> eventStubPQ;
    private final ImList<Customer> customers;

    public Simulate3(int numOfServers, List<Double> custArrivalTimes) {
        this.servers = getServers(numOfServers);
        this.eventStubPQ = getEventStubPQ(custArrivalTimes);
        this.customers = getCustomers(custArrivalTimes);
    }

    private static ImList<Customer> getCustomers(List<Double> custArrivalTimes) {
        ImList<Customer> currentCustomers = ImList.<Customer>of();
        for (int i = 0; i < custArrivalTimes.size(); i++) {
            currentCustomers = currentCustomers.add(new Customer(i, custArrivalTimes.get(i)));
        }
        return currentCustomers;
    }

    private static ImList<Server> getServers(int numOfServers) {
        ImList<Server> currentServers = ImList.<Server>of();
        for (int i = 1; i < numOfServers + 1; i++) {
            currentServers = currentServers.add(new Server(i));
        }
        return currentServers;
    }

    private static PQ<EventStub> getEventStubPQ(List<Double> custArrivalTimes) {
        PQ<EventStub> currentEventStubs = new PQ<EventStub>(new EventComparator());
        for (int i = 0; i < custArrivalTimes.size(); i++) {
            double time = custArrivalTimes.get(i);
            Customer newCustomer = new Customer(i + 1, time);
            currentEventStubs = currentEventStubs.add(new EventStub(newCustomer, time));
        }
        return currentEventStubs;
    }

    public String run() {
        PQ<EventStub> test = this.eventStubPQ;
        ImList<Server> currentServers = this.servers;
        String output = "";

        // dummy customerId for orElse()
        int j = 0;

        while (!test.isEmpty()) {
            EventStub es = test.poll().first();
            Customer customerLoop = es.getCustomer();
            EventState eventTypeCustomerLoop = customerLoop.getCurrentCustomerState();

            if (eventTypeCustomerLoop == EventState.ARRIVE) {
                Pair<Optional<Event>, Shop> arriveTest = arriveFunction(es, customerLoop, currentServers);
                Optional<Event> arriveFirst = arriveTest.first();
                output += arriveFirst.orElse(new EventStub(this.customers.get(j),
                        this.customers.get(j).getArrivalTime())) + "\n";
                // whether if is wait, done, served or leave
                Shop shopSecond = arriveTest.second();
                customerLoop = subsequentFunction(customerLoop, shopSecond);
            } else if (eventTypeCustomerLoop == EventState.SERVE) {
                Pair<Optional<Event>, Shop> serveEvent = serveFunction(es, customerLoop, currentServers);
                Optional<Event> serveFirst = serveEvent.first();
                Event newEventStub = serveFirst.orElse(new EventStub(this.customers.get(j),
                        this.customers.get(j).getArrivalTime()));
                output += newEventStub + "\n";
                Shop shopSecond = serveEvent.second();
                // update currentServers to those new servers that are busy
                currentServers = shopSecond.getImServers();
                // change to done event
                customerLoop = subsequentDoneFunction(customerLoop);
                // update es event time
                es = new EventStub(newEventStub.getCustomer(), newEventStub.getEventTime() + 1.0);
            } else if (eventTypeCustomerLoop == EventState.DONE) {
                Pair<Optional<Event>, Shop> doneEvent = doneFunction(es, customerLoop, currentServers);
                Optional<Event> doneFirst = doneEvent.first();
                output += doneFirst.orElse(new EventStub(this.customers.get(j),
                        this.customers.get(j).getArrivalTime())) + "\n";
                Shop shopSecond = doneEvent.second();
                // update currentServers to those new servers that are not busy
                currentServers = shopSecond.getImServers();
                // change to default event
                customerLoop = subsequentDefaultFunction(customerLoop);
            } else if (eventTypeCustomerLoop == EventState.LEAVE) {
                Pair<Optional<Event>, Shop> leaveEvent = leaveFunction(es, customerLoop, currentServers);
                Optional<Event> leaveFirst = leaveEvent.first();
                output += leaveFirst.orElse(new EventStub(this.customers.get(j),
                        this.customers.get(j).getArrivalTime())) + "\n";
                customerLoop = subsequentDefaultFunction(customerLoop);
            } else if (eventTypeCustomerLoop == EventState.WAIT) {
                Pair<Optional<Event>, Shop> waitEvent = waitFunction(es, customerLoop, currentServers);
                Optional<Event> waitFirst = waitEvent.first();
                Event newEventStub = waitFirst.orElse(new EventStub(this.customers.get(j),
                        this.customers.get(j).getArrivalTime()));
                output += newEventStub + "\n";
                Shop shopSecond = waitEvent.second();
                // update currentServers to those new servers that have customers waiting
                currentServers = shopSecond.getImServers();
                // change to serve event
                customerLoop = subsequentServeFunction(customerLoop);
                // update es event time
                int customerId = customerLoop.getCustomerId();
                double nextAvailableTimeForCustomerWaiting = getCustomerWaitingTime(currentServers, customerId);
                int serverServedId = getServerServedId(currentServers, customerId);
                customerLoop = new Customer(customerId,
                            nextAvailableTimeForCustomerWaiting, customerLoop.getCurrentCustomerState(),
                            serverServedId);
                es = new EventStub(customerLoop, nextAvailableTimeForCustomerWaiting);
            }

            test = test.poll().second();
            if (customerLoop.getCurrentCustomerState() != EventState.DEFAULT) {
                test = test.add(new EventStub(customerLoop, es.getEventTime()));
            }
        }
        return output + "-- End of Simulation --";
    }

    private double getCustomerWaitingTime(ImList<Server> servers, int customerId) {
        for (int i = 0; i < servers.size(); i++) {
            Server currentServer = servers.get(i);
            if (currentServer.getWaitCustomerId() == customerId) {
                return currentServer.getNextAvailableTime();
            }
        }

        return -1;
    }

    private int getServerServedId(ImList<Server> servers, int customerId) {
        for (int i = 0; i < servers.size(); i++) {
            Server currentServer = servers.get(i);
            if (currentServer.getWaitCustomerId() == customerId) {
                return currentServer.getServerId();
            }
        }

        return -1;
    }

    private Pair<Optional<Event>, Shop> arriveFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Arrive arrive = new Arrive(customer, es.getEventTime());
        Pair<Optional<Event>, Shop> arriveTest = arrive.execute(new Shop(currentServers));
        return arriveTest;
    }

    private Customer subsequentFunction(Customer customer, Shop shop) {
        ImList<Server> currentServers = shop.getImServers();
        for (int i = 0; i < currentServers.size(); i++) {
            Server currentServer = currentServers.get(i);
            if (shop.checkAllImServersBusy()) {
                // either wait or leave
                if (currentServer.getWaitCustomerId() == -1){
                    // returning wait state
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.WAIT, currentServers.get(i).getServerId());
                }
                else if (i + 1 == currentServers.size()) {
                    // returning leave state
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                                EventState.LEAVE, customer.getServerId());
                }
            } else {
                if (currentServer.canServe(customer)) {
                    // returning serve state
                    return new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                            EventState.SERVE, currentServers.get(i).getServerId());
                }
            }
        }

        return customer;
    }

    private Customer subsequentDoneFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                    EventState.DONE, customer.getServerId());
        return newCustomer;
    }

    private Customer subsequentDefaultFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                EventState.DEFAULT, customer.getServerId());
        return newCustomer;
    }

    private Customer subsequentServeFunction(Customer customer) {
        Customer newCustomer = new Customer(customer.getCustomerId(), customer.getArrivalTime(),
                EventState.SERVE, customer.getServerId());
        return newCustomer;
    }

    private Pair<Optional<Event>, Shop> serveFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Serve serve = new Serve(customer, es.getEventTime());
        return serve.execute(currentServers, customer);
    }

    private Pair<Optional<Event>, Shop> doneFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Done done = new Done(customer, es.getEventTime());
        return done.execute(new Shop(currentServers), customer, es);
    }

    private Pair<Optional<Event>, Shop> leaveFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Leave leave = new Leave(customer, es.getEventTime());
        return leave.execute(new Shop(currentServers));
    }

    private Pair<Optional<Event>, Shop> waitFunction(EventStub es, Customer customer, ImList<Server> currentServers) {
        Wait wait = new Wait(customer, es.getEventTime());
        return wait.execute(currentServers, customer);
    }

    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s", this.eventStubPQ.toString(), this.servers.toString());
    }
}
