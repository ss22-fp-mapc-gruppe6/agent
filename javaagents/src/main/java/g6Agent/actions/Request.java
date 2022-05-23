package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.services.Direction;

public class Request extends Action implements G6Action{
    /**
     * Requests a new block from a dispenser. The agent has to be in a cell adjacent to the dispenser and specify the direction to it.
     * E.g. if an agent is on (3,3) and a dispenser is on (3,4), the agent can use Request(Direction.SOUTH) to make a block appear on (3,4).
     * @param direction the direction from the dispenser in which the block should appear
     */
    public Request(Direction direction) {
        super("request", direction.getIdentifier());
    }
}