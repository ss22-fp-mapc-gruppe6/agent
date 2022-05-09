package g6Agent.actions;

import g6Agent.agents.MyTestAgent;
import g6Agent.services.ActionResult;
import g6Agent.services.Direction;


//WE DO NOT NEED THIS CLASS

/**
 *

public class MoveRandomly extends Move {
    private static final String TAG = "MoveRandomly";
    private final int randomNumber;
    private final Direction direction;

    public MoveRandomly( int number) {
        super("moveRandomly");
        if (number < 0 || number > 3) {
            throw new IllegalArgumentException();
        }
        this.randomNumber = number;
        this.direction = getDirection();
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public Direction getDirection() {
        Move move = null;
        switch (randomNumber) {
            case 0: move = new Move(Direction.s); break;
            case 1: move = new Move(Direction.w); break;
            case 2: move = new Move(Direction.e); break;
            case 3: move = new Move(Direction.n); break;
        }

        return direction;
    }


    @Override
    public void getAgentActionFeedback(ActionResult lastActionResult, MyTestAgent fbAgent, int step) {
        switch (lastActionResult) {
            case SUCCESS:
                succeededEffect(fbAgent, step);
                break;
            case FAILED:
                break;
            default:
                break;
        }
    }

    @Override
    public void succeededEffect(MyTestAgent agent, int step) {
        agent.updateGridPosition(direction.getVector(),step);
    }
}
 */