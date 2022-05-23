package g6Agent.decisionModule;

import g6Agent.services.Point;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

record Wrapper(Point point, Wrapper predecessor, double costSum, double totalCostFromStart, double minimalRemainingCost
) implements Comparable<Wrapper> {

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
        while (wrapper.predecessor != null){
            path.add(wrapper.point);
            wrapper = wrapper.predecessor;
        }
        Collections.reverse(path);
        return path;
    }

}
