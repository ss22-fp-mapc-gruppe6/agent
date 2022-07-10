package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Connect extends Action implements G6Action {
    /**
     * connects a block to another Agent
     *
     * @param otherAgentsName The agent to cooperate with.
     * @param position        The local coordinates of the block to connect.
     */
    private String otherAgentsName;
    private Point position;
    public Connect(String otherAgentsName, Point position) {
        super("connect", new Identifier(otherAgentsName), new Numeral(position.x), new Numeral(position.y));
        this.otherAgentsName = otherAgentsName;
        this.position = position;
    }
    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) throws Exception {
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        int maxDistance = perceptionAndMemory.getCurrentRole().getClearActionMaximumDistance();
        if (position.manhattanDistanceTo(new Point(0,0)) > maxDistance) return false;

        boolean isBlock = perceptionAndMemory.getBlocks().stream().anyMatch((x -> x.getCoordinates().equals(position)));
        boolean isAgent = perceptionAndMemory.getEnemyAgents().stream().anyMatch(x -> x.equals(otherAgentsName));

        return ( isBlock || isAgent);
    }
}
