package hash2021;

import kotlin.text.Charsets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static final String FILE = "b.txt";
    public static final String ROOT = "src/hash2021";
    public static final String INPUT = ROOT + "/" + FILE;
    public static final String OUTPUT = ROOT + "/out_" + FILE;


    public static void main(String[] args) throws FileNotFoundException {
        var scanner = new Scanner(new File(INPUT));

        String[] firstLine = scanner.nextLine().split(" ");
        var DURATION = Integer.parseInt(firstLine[0]);
        var I = Integer.parseInt(firstLine[1]);
        var S = Integer.parseInt(firstLine[2]);
        var V = Integer.parseInt(firstLine[3]);
        var BONUS = Integer.parseInt(firstLine[4]);

        var intersections = IntStream.range(0, I)
                .boxed()
                .map(it -> new AbstractMap.SimpleEntry<>(it, new Intersection(it)))
                .collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue()));

        var streets = new HashMap<String, Street>(S);
        for (int i = 0; i < S; i++) {
            String[] streetLine = scanner.nextLine().split(" ");
            var B = Integer.parseInt(streetLine[0]);
            var E = Integer.parseInt(streetLine[1]);
            var name = streetLine[2];
            var L = Integer.parseInt(streetLine[3]);

            streets.put(name, new Street(name, B, E, L));
        }

        var cars = new ArrayList<Car>(V);
        for (int i = 0; i < V; i++) {
            String[] pathLine = scanner.nextLine().split(" ");
            var P = Integer.parseInt(pathLine[0]);
            String[] route = Arrays.copyOfRange(pathLine, 1, pathLine.length);

            var routeStreets = new LinkedList<Pair<Street, Intersection>>();
            Stream.of(route)
                    .map(streets::get)
                    .map(street -> new Pair<>(street, intersections.get(street.end)))
                    .collect(Collectors.toCollection(() -> routeStreets));
            Integer sumLength = routeStreets.stream()
                    .map(it -> it.first().length)
                    .reduce((l1, l2) -> l1 + l2)
                    .orElse(0) - routeStreets.get(0).first().length;
            if (sumLength > DURATION) {
                System.err.println("CAR IMPOSSIBLE");
                continue;
            } else if (DURATION - sumLength < DURATION / 10) {
                System.err.println("CAR HARDLY POSSIBLE");
                continue;
            }
            Car car = new Car(i, routeStreets);
            cars.add(car);

            // initial cars position put to intersection
            Pair<Street, Intersection> position = car.getCurrentStreet();
            Street street = position.first();
            Intersection intersection = position.second();
            intersection.addCar(street.name, car);
        }

        for (Intersection intersection : intersections.values()) {
            intersection.applyTick();
        }

        // simulation
        for (int i = 1; i < DURATION + 1; i++) {
            System.out.printf("tick %d of %d\n", i, DURATION + 1);
            for (Intersection intersection : intersections.values()) {
                intersection.tick(i);
            }

            for (Intersection intersection : intersections.values()) {
                intersection.applyTick();
            }
        }

        // output
        HashMap<Integer, List<Pair<String, Integer>>> result = new HashMap<>();
        // v1
//        List<Item> scores = new ArrayList<>();
//        for (Intersection intesection : intersections.values()) {
//            List<Item> intersectionScore = intesection.getScore();
//            scores.addAll(intersectionScore);
//            if (!intersectionScore.isEmpty()) {
//                List<Pair<String, Integer>> pairs = result.computeIfAbsent(intesection.id, it -> new LinkedList<>());
//
//                Long smallestScore = null;
//                for (Item item : intersectionScore) {
//                    if (smallestScore == null || item.score < smallestScore) {
//                        smallestScore = item.score;
//                    }
//                }
//
//                for (Item item : intersectionScore) {
//                    pairs.add(new Pair<>(item.street, (int) Math.ceil(item.score / (double) smallestScore)));
//                }
//            }
//        }

//        Misha.save(scores);

        // v2
        for (Intersection intersection : intersections.values()) {
            List<Pair<String, Integer>> scoreV2 = intersection.getScoreV2();
            if (!scoreV2.isEmpty()) {
                result.put(intersection.id, scoreV2);
            }
        }



//        for (Item score : scores) {
//            System.out.println(score.toString());
//        }

        // output result
        writeToFile(OUTPUT, o -> {
            PrintWriter out = o;
            out.println(result.size());
            result.forEach((id, pairs) -> {
                out.println(id);
                out.println(pairs.size());
                for (Pair<String, Integer> pair : pairs) {
                    Integer second = pair.second();
                    out.println(pair.first() + " " + second);
                }
            });
        });

        PrintStream out = System.out;
        out.println(result.size());
        result.forEach((id, pairs) -> {
            System.out.println(id);
            System.out.println(pairs.size());
            for (Pair<String, Integer> pair : pairs) {
                Integer second = pair.second();
                System.out.println(pair.first() + " " + second);
            }
        });
    }

    public static void writeToFile(String fileName, Consumer<PrintWriter> out) throws FileNotFoundException {
        Charset charset = Charsets.UTF_8;
        int bufferSize = 8 * 1024;
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), charset), bufferSize))) {
            out.accept(printWriter);
        }
    }


    public static class Intersection {
        private final int id;
        private final Map<String, LinkedList<Car>> streets = new HashMap<>(); // street -> (car.id -> car)
        private final List<Triple<Integer, String, Integer>> statistics = new LinkedList<>(); // (time, street, numberOfCars)

        private final LinkedList<Pair<String, Car>> buffer = new LinkedList<>();
        private int currentTime;

        public Intersection(int id) {
            this.id = id;
        }

        public void addCar(String street, Car car) {
            buffer.add(new Pair<>(street, car));
        }

        public void applyTick() {
            for (Pair<String, Car> pair : buffer) {
                String street = pair.first();
                Car car = pair.second();

                LinkedList<Car> carsOnStreet = streets.computeIfAbsent(street, it -> new LinkedList<>());
                carsOnStreet.addLast(car);
            }
            buffer.clear();

            // write statistics
            streets.forEach((street, cars) -> {
                int count = (int) cars.stream().filter(Car::isWaiting).count();
                statistics.add(new Triple<>(currentTime, street, count));
            });
        }

        public void tick(int time) {
//            System.out.printf("time period %d for intersection %d\n", time, this.id);
            this.currentTime = time;
            streets.forEach((streetName, cars) -> {
                ListIterator<Car> iterator = cars.listIterator();
                while (iterator.hasNext()) {
                    Car car = iterator.next();
                    boolean finished = car.move();
                    if (finished) {
                        iterator.remove();
//                        System.out.printf("car %d finished at %d\n", car.id, this.id);
                    } else {
                        Pair<Street, Intersection> position = car.getCurrentStreet();
                        Street carStreet = position.first();
                        if (!carStreet.name.equals(streetName)) {
                            Intersection intersection = position.second();
                            intersection.addCar(carStreet.name, car);
                            iterator.remove();
//                            System.out.printf("move car %d from %d to %d\n", car.id, this.id, intersection.id);
                        }
                    }
                }
            });

        }

        public List<Item> getScore() {
            long total = 0;
            HashMap<String, Long> scorePerStreet = new HashMap<>();
            statistics.forEach(stat -> {
                Integer time = stat.first();
                String streetName = stat.second();
                Integer count = stat.third();
                Long numOfCars = scorePerStreet.computeIfAbsent(streetName, it -> 0L);
                scorePerStreet.put(streetName, numOfCars + count);
            });

            for (Long value : scorePerStreet.values()) {
                total += value;
            }
            long finalTotal = total;

            ArrayList<Item> items = new ArrayList<>(scorePerStreet.size());
            scorePerStreet.forEach((street, numOfCars) -> {
                items.add(new Item(this.id, street, numOfCars, finalTotal));
            });
            return items;
        }

        public List<Pair<String, Integer>> getScoreV2() {
            HashMap<String, List<Integer>> countPerStreet = new HashMap<>();
            statistics.forEach(stat -> {
                Integer time = stat.first();
                String streetName = stat.second();
                Integer count = stat.third();
                if (count > 0) {
                    List<Integer> numOfCars = countPerStreet.computeIfAbsent(streetName, it -> new LinkedList<>());
                    numOfCars.add(count);
                }
            });

            double intersectionTotal = 0;
            Double intersectionMin = null;
            List<Triple<String, Integer, Integer>> streetsInfo = new ArrayList<>(countPerStreet.size());
            for (Map.Entry<String, List<Integer>> entry : countPerStreet.entrySet()) {
                String street = entry.getKey();
                List<Integer> counts = entry.getValue();
                double avg = counts.stream().mapToInt(it -> it).average().orElse(1);
                double sum = counts.stream().mapToInt(it -> it).sum();
                System.out.printf("intersection %d avg: %.2f sum: %.2f\n", this.id, avg, sum);
                avg = Math.round(avg);
                streetsInfo.add(new Triple<>(street, (int) avg, (int) sum));
                intersectionTotal += sum;
                if (intersectionMin == null || sum < intersectionMin) {
                    intersectionMin = sum;
                }
            }
            double intersectionAvg = intersectionTotal / streets.keySet().size();
            double filterThreshold = 0;

            List<Pair<String, Integer>> result = new ArrayList<>(countPerStreet.size());
            for (Triple<String, Integer, Integer> triple : streetsInfo) {
                String street = triple.first();
                Integer avg = triple.second();
                Integer sum = triple.third();

                int score = 1;
                if (avg * sum > intersectionAvg) {
                    score = 2;
                }
                if (intersectionAvg > 5) {
                    if (sum > filterThreshold) {
                        result.add(new Pair<>(street, score));
                    } else {
                        System.out.println("skip");
                    }
                } else {
                    result.add(new Pair<>(street, score));
                }
            }
            return result;
        }

    }

    public static class Car {
        private final int id;
        private final LinkedList<Pair<Street, Intersection>> route;
        private final int numberOfStreets;

        private int position = 1; // on street

        public Car(int id, LinkedList<Pair<Street, Intersection>> route) {
            this.id = id;
            this.route = route;
            this.numberOfStreets = route.size();
        }

        public Pair<Street, Intersection> getCurrentStreet() {
            return route.getFirst();
        }

        public int getPosition() {
            return position;
        }

//        public int minimumTravelTime() {
//            var time = 0;
//            for (var i = 0; i < numberOfStreets; i++) {
//                time += streets.get(route[i]).length;
//            }
//            return time;
//        }

        public boolean isWaiting() {
            return position == 1;
        }

        public boolean move() {
            if (route.isEmpty()) {
                return true;
            }

            if (position <= 1) {
                route.removeFirst(); // move to next street
                boolean finished = route.isEmpty();
                if (!finished) {
                    position = getCurrentStreet().first().length;
                }
                return finished;
            } else {
                position--; // move through current street
                return false;
            }
        }
    }

    public static class Street {
        private final String name;
        private final int start; // first intersection - B
        private final int end; // second intersection - E
        private final int length; // the time car to get from  start to end of that street - L

        public Street(String name, int start, int end, int length) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.length = length;
        }
    }

    public static class Item {
        private int intersection;
        private String street;
        private long score;
        private long total;


        public Item(int intersection, String street, long score, long total) {
            this.intersection = intersection;
            this.street = street;
            this.score = score;
            this.total = total;
        }

        public long getScore() {
            return score;
        }

        public int getIntersection() {
            return intersection;
        }

        public String getStreet() {
            return street;
        }

        @Override
        public String toString() {
            return "intersection=" + intersection +
                    ", street='" + street + '\'' +
                    ", score=" + score;
        }
    }


}
