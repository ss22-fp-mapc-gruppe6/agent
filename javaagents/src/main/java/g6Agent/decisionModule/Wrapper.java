package g6Agent.decisionModule;

import g6Agent.services.Point;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

final class Wrapper implements Comparable<Wrapper> {
    private final Point point;
    private final Wrapper predecessor;
    private final double costSum;
    private final double totalCostFromStart;
    private final double minimalRemainingCost;

    Wrapper(Point point, Wrapper predecessor, double costSum, double totalCostFromStart, double minimalRemainingCost
    ) {
        this.point = point;
        this.predecessor = predecessor;
        this.costSum = costSum;
        this.totalCostFromStart = totalCostFromStart;
        this.minimalRemainingCost = minimalRemainingCost;
    }

    public static Wrapper create(Point point, Wrapper predecessor, double totalCostFromStart, double minimumRemainingCost) {
        return new Wrapper(point, predecessor, totalCostFromStart + minimumRemainingCost, totalCostFromStart, minimumRemainingCost);
    }

    @Override
    public int compareTo(Wrapper o) {
        final var i = Double.compare(this.costSum, o.costSum);
        return i;
    }


    List<Point> tracePath() {
        final var path = new LinkedList<Point>();
        var wrapper = this;
        while (wrapper.predecessor != null) {
            path.add(wrapper.point);
            wrapper = wrapper.predecessor;
        }
        Collections.reverse(path);
        return path;
    }

    public Point point() {
        return point;
    }

    public Wrapper predecessor() {
        return predecessor;
    }

    public double costSum() {
        return costSum;
    }

    public double totalCostFromStart() {
        return totalCostFromStart;
    }

    public double minimalRemainingCost() {
        return minimalRemainingCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Wrapper) obj;
        return Objects.equals(this.point, that.point) &&
                Objects.equals(this.predecessor, that.predecessor) &&
                Double.doubleToLongBits(this.costSum) == Double.doubleToLongBits(that.costSum) &&
                Double.doubleToLongBits(this.totalCostFromStart) == Double.doubleToLongBits(that.totalCostFromStart) &&
                Double.doubleToLongBits(this.minimalRemainingCost) == Double.doubleToLongBits(that.minimalRemainingCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, predecessor, costSum, totalCostFromStart, minimalRemainingCost);
    }

    @Override
    public String toString() {
        return "Wrapper[" +
                "point=" + point + ", " +
                "predecessor=" + predecessor + ", " +
                "costSum=" + costSum + ", " +
                "totalCostFromStart=" + totalCostFromStart + ", " +
                "minimalRemainingCost=" + minimalRemainingCost + ']';
    }


}
