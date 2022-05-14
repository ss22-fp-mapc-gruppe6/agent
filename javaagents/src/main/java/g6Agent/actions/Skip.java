package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;

// Agent won't do anything this turn.
public class Skip extends Action implements g6Action{
    private static final String TAG = "Skip";

    public Skip() {
        super("skip");
    }


    @Override
    public void setSucceededEffect(MyTestAgent agent, int step) {

    }
}
