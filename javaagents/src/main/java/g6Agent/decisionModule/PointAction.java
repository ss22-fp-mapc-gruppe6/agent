package g6Agent.decisionModule;

import eis.iilang.Action;
import g6Agent.actions.G6Action;
import g6Agent.services.Point;
import org.jetbrains.annotations.NotNull;

public record PointAction(Point location, G6Action action, Point target) implements Comparable<PointAction>{
    @Override
    public String toString() {
        final String stringFrom = location != null ? location.toString() : "";
        final String stringAction = action != null ? ((Action) action).toProlog() : "";
        final String stringTo = target != null ? target.toString() : "";
        return stringFrom + stringAction + stringTo;
    }

    @Override
    public int compareTo(@NotNull PointAction o) {
        int compareLocation = location.compareTo(o.location);
        if (compareLocation == 0) {
            return target.compareTo(o.target);
        } else {
            return compareLocation;
        }
    }
}
