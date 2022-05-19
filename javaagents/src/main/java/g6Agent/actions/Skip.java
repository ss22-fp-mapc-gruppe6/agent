package g6Agent.actions;

import eis.iilang.Action;

// Agent won't do anything this turn.
public class Skip extends Action implements G6Action {
    /**
     * The agent won't do anything this turn. Always successful (except for random fail).
     */
    public Skip() {
        super("skip");
    }

}
