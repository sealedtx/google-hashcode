package hash2021;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Misha {

    static int threshhold = 20;
    static int intersection = 0;

    public static void save(List<Main.Item> items) {

        List<Main.Item> toRemove = new LinkedList<>();

        for (Main.Item item : items) {
            if (checkSuitability(item.getScore())) {
                toRemove.add(item);
            }
        }
        items.removeAll(toRemove);

        List<Intersection> intersections = new ArrayList<>();

        for (Main.Item item : items) {
            intersection = item.getIntersection();
            for (Intersection i : intersections) {
                if (item.getIntersection() != i.getId()) {
                    intersections.add(new Intersection(intersection, item.getStreet(), item.getScore()));
                } else {
                    i.getStreets().add(item.getStreet());
                    i.getScores().add(item.getScore());
                }
            }
        }
        calculatePartials(intersections);

        //output here
        System.out.println(intersections.size());

        for (Intersection i : intersections
        ) {
            System.out.println(i.getId());
            System.out.println(i.getStreets().size());
            int count = 0;
            for (String street : i.getStreets()
            ) {
                System.out.println(street + i.getSeconds().get(count));
                count++;
            }
        }
    }

    public static void calculatePartials(List<Intersection> intersections) {
        for (Intersection i : intersections) {
            int scorePerIntersection = 0;
            for (Long score : i.getScores()) {
                scorePerIntersection += score;
            }

            for (int j = 0; j < i.getScores().size(); j++) {
                float coeff = (1 / scorePerIntersection) * (i.getScores().get(j));
                i.getCoeffs().add(coeff);
            }

            Collections.sort(i.getCoeffs());
            float min = i.getCoeffs().get(0);
            for (int j = 1; j < i.getCoeffs().size(); j++) {
                float second = i.getCoeffs().get(j) / min;
                int doneSecond = (int) Math.ceil(second);
                i.getSeconds().add(doneSecond);
            }
        }
    }

    public static boolean checkSuitability(long score) {
        return score < threshhold;
    }
}
