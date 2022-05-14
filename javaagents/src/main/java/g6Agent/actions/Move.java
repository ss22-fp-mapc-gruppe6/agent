package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Direction;

// Agent moves in the specified direction (north, west, est south)
public class Move extends Action implements g6Action{
    private static final String TAG = "Move";
    private Direction direction;
    private PerceptionAndMemory perception;

    public Move(Direction direction, String name) {
        super(name);
        this.direction = direction;
        perception = new PerceptionAndMemoryImplementation();
    }

    public Move(Direction s) {
        super("move");
    }


    public Direction getDirection() {
        return direction;
    }



    @Override
    public void setSucceededEffect(MyTestAgent agent, int step) {
        if (perception.getLastAction().getSuccessMessage().equals("success")) {
            agent.updateGridPosition(direction.getNextCoordinate(), step);
        }
    }

}
