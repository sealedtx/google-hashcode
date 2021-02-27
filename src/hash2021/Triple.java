package hash2021;

import java.util.Objects;

public class Triple<A, B, C> {
    private A first;
    private B second;
    private C third;

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public C third() {
        return third;
    }

    public void setSecond(B value) {
        this.second = value;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setThird(C third) {
        this.third = third;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Triple))
            return false;
        Triple<?, ?, ?> e = (Triple<?, ?, ?>) o;
        return eq(first, e.first()) && eq(second, e.second()) && eq(third, e.third());
    }

    private static boolean eq(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^
                (second == null ? 0 : second.hashCode()) ^
                (third == null ? 0 : third.hashCode());
    }

    public String toString() {
        return first + ", " + second + ", " + third;
    }

}