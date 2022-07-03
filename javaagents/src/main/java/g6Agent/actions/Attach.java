package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.services.Direction;

// Attaches a block to the agent.
public class Attach extends Action implements G6Action {

    /**
     * Attaches a thing (friendly entity, block or obstacle) to the agent. The agent has to be directly beside the thing.
     *
     * @param direction the direction of the thing to attach
     */
    public Attach(Direction direction) {
        super("attach", direction.getIdentifier());


    }


}
