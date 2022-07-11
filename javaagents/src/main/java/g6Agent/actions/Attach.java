package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;

// Attaches a block to the agent.
public class Attach extends Action implements G6Action {
    private final Direction direction;

    /**
     * Attaches a thing (friendly entity, block or obstacle) to the agent. The agent has to be directly beside the thing.
     *
     * @param direction the direction of the thing to attach
     */
    public Attach(Direction direction) {
        super("attach", direction.getIdentifier());
 this.direction = direction;

    }
    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory){
       return perceptionAndMemory.getBlocks().stream().anyMatch((x -> x.getCoordinates().equals(direction.getNextCoordinate())));

    }

}
