package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.services.ActionResult;
import g6Agent.services.Direction;

// Agent moves in the specified direction (north, west, est south)
public class Move extends Action implements g6Action{
    private static final String TAG = "Move";
    private Direction direction;

    public Move(Direction direction, String name) {
        super(name);
        this.direction = direction;
    }

    public Move(Direction s) {
        super("move");
    }


    public Direction getDirection() {
        return direction;
    }

    @Override
    public void getAgentActionFeedback(ActionResult lastActionResult, MyTestAgent Agent, int step) {
        switch (lastActionResult) {
            case SUCCESS:
                succeededEffect(Agent, step);
                break;
            case FAILED_PARAMETER:
                break;
            case FAILED_PATH:
                break;
            case FAILED:
                break;
            default:
                break;
        }
    }

    @Override
    // todo lastaction get involved
    public void succeededEffect(MyTestAgent agent, int step) {
        agent.updateGridPosition(direction.getVector(),step);
    }

}
