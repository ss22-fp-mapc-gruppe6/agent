package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Connect extends Action implements G6Action {
    private final String otherAgentsName;
    private final Point position;
    /**
     * connects a block to another Agent
     *
     * @param otherAgentsName The agent to cooperate with.
     * @param position        The local coordinates of the block to connect.
     */
    public Connect(String otherAgentsName, Point position) {
        super("connect", new Identifier(otherAgentsName), new Numeral(position.x), new Numeral(position.y));
        this.otherAgentsName = otherAgentsName;
        this.position = position;
    }
    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) {

        boolean isBlock = perceptionAndMemory.getDirectlyAttachedBlocks().stream().anyMatch((x -> x.getCoordinates().equals(position)));
        Point agentPos = perceptionAndMemory.getPositionOfKnownAgent(otherAgentsName);
        if (agentPos == null) return false;
        return ( isBlock && position.isAdjacentTo(agentPos));
    }
}
