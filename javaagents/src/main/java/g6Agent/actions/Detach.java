package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;

/**
 * A Detach Action
 */
public class Detach extends Action implements G6Action {

    private final Direction direction;

    /**
     * Detaches a thing from the agent. Only the connection between the agent and the thing is released.
     *
     * @param direction the direction an Block should be detached from
     */
    public Detach(Direction direction) {
        super("detach", direction.getIdentifier());
        this.direction = direction;
    }
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) {
       return perceptionAndMemory.getDirectlyAttachedBlocks()
                .stream()
                .anyMatch((x -> x.getCoordinates().equals(direction.getNextCoordinate())));
    }

}
