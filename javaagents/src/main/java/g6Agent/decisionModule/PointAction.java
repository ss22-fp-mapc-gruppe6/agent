package g6Agent.decisionModule;

import eis.iilang.Action;
import g6Agent.actions.G6Action;
import g6Agent.services.Point;

public record PointAction(Point location, G6Action action, Point target) {
    @Override
    public String toString() {
        final String stringFrom = location != null ? location.toString() : "";
        final String stringAction = action != null ? ((Action) action).toProlog() : "";
        final String stringTo = target != null ? target.toString() : "";
        return stringFrom + stringAction + stringTo;
    }
}
