package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.actions.g6Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Rotation;

public class Rotate extends Action implements g6Action {
 private PerceptionAndMemory perception;
 private Rotation rotation;

    public Rotate(Rotation rotation) {
        super("rotate");
        this.rotation = rotation;
    }

    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
      this.perception = perception;

      perception.getLastAction().setSuccessfulMessage("success");
        perception.getLastAction().setName("rotate");
    }
}
