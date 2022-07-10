package g6Agent.decisionModule;

import eis.iilang.Action;
import eis.iilang.Identifier;
import g6Agent.actions.G6Action;
import g6Agent.services.Point;
import org.jetbrains.annotations.NotNull;

public record PointAction(Point location, G6Action action, Point target) implements Comparable<PointAction> {
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
        if (compareLocation != 0) return compareLocation;
        else {
            int compareTarget = target.compareTo(o.target);
            if (compareTarget != 0) return compareTarget;
            else {
                String name = ((Action) action).getName();
                String nameO = ((Action) o.action).getName();
                int compareName = name.compareTo(nameO);
                if (compareName != 0) return compareName;
                else {
                    Object[] parameters = ((Action) action).getParameters().toArray();
                    Object[] parametersO = ((Action) o.action).getParameters().toArray();
                    int compareParameterLength = Integer.compare(parameters.length, parametersO.length);
                    if (compareParameterLength == 0) return compareParameterLength;
                    else {
                        for (int i = 0; i < parameters.length; i++) {
                            String value = ((Identifier) parameters[i]).getValue();
                            String value0 = ((Identifier) parametersO[i]).getValue();
                            int compareParameter = value.compareTo(value0);
                            if (compareParameter != 0) return compareParameter;
                        }
                        return 0;
                    }

                }
            }
        }
    }
}
