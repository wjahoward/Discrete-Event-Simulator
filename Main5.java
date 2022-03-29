import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.function.Supplier;
import cs2030.util.Pair;
import cs2030.simulator.Simulate5;

class Main5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Pair<Double, Supplier<Double>>> inputTimes;

        int numOfServers = sc.nextInt();
        int numOfCustomers = sc.nextInt();

        inputTimes = Stream.<Pair<Double, Supplier<Double>>>generate(() -> 
                Pair.of(sc.nextDouble(), () -> sc.nextDouble()))
            .limit(numOfCustomers)
            .collect(Collectors.toUnmodifiableList());

        // this idea conveys that if the first element did not use it
        // the second or third element still able to use that value
        // for instance, if the service time for the first element supposedly is 1.5
        // if the first element did not use it,
        // the second element is able to use that service time of 1.5
//        System.out.println(inputTimes.get(0).first());
//        System.out.println(inputTimes.get(0).second().get());
//        System.out.println(inputTimes.get(2).first());
//        System.out.println(inputTimes.get(2).second().get());
        Simulate5 sim = new Simulate5(numOfServers, inputTimes);
        System.out.println(sim.run());
    }
}
