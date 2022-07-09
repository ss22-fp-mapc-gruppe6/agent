package g6Agent.decisionModule.astar;

import g6Agent.decisionModule.PointAction;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

import java.util.*;

@With
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
final class Wrapper implements Comparable<Wrapper> {
    @EqualsAndHashCode.Include
    final PointAction pointAction;
    final Wrapper predecessor;
    final double costSum;
    final double totalCostFromStart;
    final double minimalRemainingCost;
    final Set<Point> destroyedObstacles;
    final int step;
    List<Point> attachments;
    final Direction compass;
    final Stack<Wrapper> postponed;

    @Override
    public String toString() {
        return "(" + pointAction + ")-" + costSum;
    }

    @Override
    public int compareTo(Wrapper o) {
        final var i = Double.compare(this.costSum, o.costSum);
        return i;
    }


    List<PointAction> tracePath() {
        final List<PointAction> path = new LinkedList<>();
        var wrapper = this;
        while (wrapper.predecessor != null) {
//                path.add(new PointAction(wrapper.pointAction, wrapper.action));
            path.add(wrapper.pointAction);
            wrapper = wrapper.predecessor;
        }
        Collections.reverse(path);
        return path;
    }
}
