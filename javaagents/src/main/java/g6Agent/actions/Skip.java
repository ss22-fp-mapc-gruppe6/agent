package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

// Agent won't do anything this turn.
public class Skip extends Action implements g6Action{
    private static final String TAG = "Skip";
    private PerceptionAndMemory perception;


    public Skip() {
        super("skip");
    }
    @Override
    public void setSucceededEffect(MyTestAgent agent, int step,PerceptionAndMemory perception) {
        this.perception = perception;
        perception.getLastAction().setSuccessfulMessage("success");
        perception.getLastAction().setName("skip");
    }
}
