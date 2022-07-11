package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

// Agent won't do anything this turn.
public class Skip extends Action implements G6Action {
    /**
     * The agent won't do anything this turn. Always successful (except for random fail).
     */
    public Skip() {
        super("skip");
    }


    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) {
        return true;
    }
}
