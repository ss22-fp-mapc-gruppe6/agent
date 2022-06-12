package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Disconnect extends Action implements g6Action{

    private PerceptionAndMemory perception;
    private final Point from;
    private final Point to;

    public Disconnect(Point from, Point to) {
        super("disconnect");
        this.from = from;
        this.to = to;
    }
    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
        this.perception = perception;
            perception.getLastAction().setSuccessfulMessage("success");
            perception.getLastAction().setName("move");
        }

}
