package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;
import g6Agent.environment.GridObject;

import java.util.HashMap;

public class G6GoalDig implements Goal {
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalDig(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
        //subgoal: go in dig range (of current role)
        //subgoal: destroy obstacles
    }

    @Override
    public G6Action getNextAction() {
        if(perceptionAndMemory.getObstacles().isEmpty()){
            return new Skip();
        }

        // when energy is less or equal to 1, agent cant clear any entity
        if(perceptionAndMemory.getEnergy() <= 1){
            return new Skip();
        }

        //find closest obstacle
        Point closestObstacle = perceptionAndMemory.getObstacles().get(0);
        for(Point obstacle : perceptionAndMemory.getObstacles()){

            if (obstacle.manhattanDistanceTo(new Point(0,0)) < closestObstacle.manhattanDistanceTo(new Point(0,0))){
                closestObstacle = obstacle;
            }
        }
        //if in Range -> clear
        if(perceptionAndMemory.getCurrentRole() != null) {
            if (closestObstacle.manhattanDistanceTo(new Point(0, 0)) <= perceptionAndMemory.getCurrentRole().getClearActionMaximumDistance()) {
                return new Clear(closestObstacle);
            }
        }

        //find best way to obstacle
        Direction direction = Direction.WEST;
        for (Direction d : Direction.allDirections()){
            if (d.getNextCoordinate().manhattanDistanceTo(closestObstacle) < direction.getNextCoordinate().manhattanDistanceTo(closestObstacle)){
                direction = d;
            }
        }
        return new Move(direction);
    }

    @Override
    public boolean isSucceding() {
        return !perceptionAndMemory.getObstacles().isEmpty();
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalDig";
    }

    @Override
    public boolean preconditionsMet() {
        return !perceptionAndMemory.getObstacles().isEmpty();
    }
}
