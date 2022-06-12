package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;

public class Factory_g6Action extends Action {

    public Factory_g6Action(String name) {
        super(name);
    }

    public static Action getActionClass(String actionClass, Direction direction) {
        switch (actionClass) {
            case "Move":
                return new Move(direction, actionClass);
            case "Skip":
                return new Skip();


            default:
                return null;
        }
    }
}
