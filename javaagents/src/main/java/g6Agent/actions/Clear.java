package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Numeral;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Clear extends Action implements G6Action {
    /**
     * Clears an obstacle or disables an enemy agent at the given position
     *
     * @param obstacle the position
     */

    private Point obstacle;
    public Clear(Point obstacle) {
        super("clear", new Numeral(obstacle.x), new Numeral(obstacle.y));
        this.obstacle = obstacle;
    }

    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) throws Exception {
        boolean isObstacle = perceptionAndMemory.getObstacles().stream().anyMatch(x -> x.equals(obstacle));
        boolean isBlock = perceptionAndMemory.getBlocks().stream().anyMatch((x -> x.getCoordinates().equals(obstacle)));
        boolean isAgent = perceptionAndMemory.getEnemyAgents().stream().anyMatch(x -> x.equals(obstacle));

        return (isObstacle || isBlock || isAgent);
    }
}
