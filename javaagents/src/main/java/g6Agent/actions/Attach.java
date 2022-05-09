package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.environment.CellObject;
import g6Agent.services.ActionResult;
import g6Agent.services.Direction;
import g6Agent.services.Point;

// Attaches a block to the agent.
public class Attach extends Action implements g6Action{
    private static final String TAG = "Attach";
    private final Direction direction;

    public Attach(Direction direction, String name) {
        super(name);

        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;}

    @Override
    public void getAgentActionFeedback(ActionResult lastActionResult, MyTestAgent Agent, int step) {
        switch (lastActionResult) {
            case SUCCESS:
                changesAfterSuccess(Agent, step);
                break;
            case FAILED_PARAMETER:
                //to do: Error Message
                break;
            case FAILED_TARGET:
                //to do: Error Message
                break;
            case FAILED_BLOCKED:
                //to do: Error Message
                break;
            case FAILED:
                //to do: Error Message
                break;
            default:
                break;
        }
    }

    @Override
    public void succeededEffect(MyTestAgent agent, int step) {

    }

    public void changesAfterSuccess(MyTestAgent agent, int step) {
        Point direction = this.direction.getDirection();
        Point agentPosition = agent.getPosition(step);
        Point attaching = agentPosition.addAll(direction);

       //todo not yet finish

    }
}
