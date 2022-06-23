package g6Agent.decisionModule;

import g6Agent.actions.G6Action;
import g6Agent.services.Point;

public record PointAction(Point from, Class<? extends G6Action> action, Point to) {
    @Override
    public String toString() {
        final String stringFrom = from != null ? from.toString() : "";
        final String stringAction = action != null ? action.getSimpleName() : "";
        final String stringTo = to != null ? to.toString() : "";
        return stringFrom + stringAction + stringTo;
    }
}
