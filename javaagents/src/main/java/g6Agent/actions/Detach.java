package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;

/**
 * A Detach Action
 */
public class Detach extends Action implements G6Action {
    /**
     * Detaches a thing from the agent. Only the connection between the agent and the thing is released.
     *
     * @param direction the direction an Block should be detached from
     */
    private Direction direction;
    public Detach(Direction direction) {
        super("detach", direction.getIdentifier());
        this.direction = direction;
    }
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) throws Exception {
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        boolean isAttachedBlock = perceptionAndMemory.getAttachedBlocks().stream().anyMatch((x -> x.getCoordinates().equals(direction)));
        return  isAttachedBlock;
    }

}
