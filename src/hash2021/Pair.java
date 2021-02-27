package hash2021;

import java.util.Objects;

public class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public void setSecond(B value) {
        this.second = value;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair))
            return false;
        Pair<?, ?> e = (Pair<?, ?>) o;
        return eq(first, e.first()) && eq(second, e.second());
    }

    private static boolean eq(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^
                (second == null ? 0 : second.hashCode());
    }

    public String toString() {
        return first + ", " + second;
    }

}