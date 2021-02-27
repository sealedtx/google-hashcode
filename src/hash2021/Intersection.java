package hash2021;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private int id = 0;
    private List<String> streets = new ArrayList<>();
    private List<Long> scores = new ArrayList<>();
    private List<Float> coeffs = new ArrayList<>();
    private List<Integer> seconds = new ArrayList<>();

    Intersection(int id, String street, long score){
        this.id = id;
        streets.add(street);
        scores.add(score);
    }

    public List<String> getStreets() {
        return streets;
    }

    public List<Long> getScores() {
        return scores;
    }

    public List<Float> getCoeffs() {
        return coeffs;
    }

    public List<Integer> getSeconds() {
        return seconds;
    }

    public int getId() {
        return id;
    }
}
