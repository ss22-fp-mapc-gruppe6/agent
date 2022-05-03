package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.services.ActionResult;

// Agent won't do anything this turn.
public class Skip extends Action implements g6Action{
    private static final String TAG = "Skip";

    public Skip() {
        super("skip");
    }

    @Override
    public void getAgentActionFeedback(ActionResult lastActionResult, MyTestAgent Agent, int step) {

    }

    @Override
    // todo lastaction get involved
    public void succeededEffect(MyTestAgent agent, int step) {

    }
}
