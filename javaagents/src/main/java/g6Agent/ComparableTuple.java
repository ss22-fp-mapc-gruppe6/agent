package g6Agent;

import org.jetbrains.annotations.NotNull;

public record ComparableTuple<A extends Comparable<A> ,B extends Comparable<B>>(A a, B b)  implements Comparable<ComparableTuple<A,B>>{
    @Override
    public int compareTo(@NotNull ComparableTuple<A, B> o) {
        int compareA = a.compareTo(o.a);
        if (compareA == 0) {
            return b.compareTo(o.b);
        } else {
            return compareA;
        }
    }
}
