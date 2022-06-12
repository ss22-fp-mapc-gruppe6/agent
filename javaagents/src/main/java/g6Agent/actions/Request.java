package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;

public class Request extends Action implements g6Action{
    private PerceptionAndMemory perception;
    private final Direction direction;

    public Request(Direction direction) {
        super("request");
        this.direction = direction;
    }
     public void setPerception(PerceptionAndMemory perception){
         this.perception = perception;
     }
    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
        perception.getLastAction().setSuccessfulMessage("success");
        perception.getLastAction().setName("request");
    }
}
