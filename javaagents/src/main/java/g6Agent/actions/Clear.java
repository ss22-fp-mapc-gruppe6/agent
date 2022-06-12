package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Clear extends Action implements g6Action{
    private PerceptionAndMemory perception;
    private final Point point;
    private  int energyBefore;

    public Clear(Point point, MyTestAgent agent) {
        super("clear");
        this.point = point;
        this.energyBefore = agent.getEnergy();
    }

    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
        this.perception = perception;
// todo create register
            perception.getLastAction().setSuccessfulMessage("success");
            perception.getLastAction().setName("clear");
    }
}
