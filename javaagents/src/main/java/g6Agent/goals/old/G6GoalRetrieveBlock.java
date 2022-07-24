package g6Agent.goals.old;

import g6Agent.actions.*;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;
import org.jetbrains.annotations.Nullable;

public class G6GoalRetrieveBlock implements Goal {
    private final PerceptionAndMemory perceptionAndMemory;
    private Block lastDispenserMovedTo;
    private Block lastBlockMovedTo;

    public G6GoalRetrieveBlock(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {

        Clear blockingobstacle = IfRotateFailedBreakFree();
        if (blockingobstacle != null) return blockingobstacle;
        if (!perceptionAndMemory.getBlocks().isEmpty()) {
            //determine next block
            Block closestBlock = perceptionAndMemory.getBlocks().get(0);
            for (Block block : perceptionAndMemory.getBlocks()) {
                if (block.getCoordinates().manhattanDistanceTo(new Point(0, 0)) < closestBlock.getCoordinates().manhattanDistanceTo(new Point(0, 0))) {
                    boolean isNotCloseToOtherAgent = checkIfNotCloseToOtherAgent(block);
                    if (isNotCloseToOtherAgent) {
                        closestBlock = block;
                    }
                }
            }
            if (checkIfNotCloseToOtherAgent(closestBlock)) {
                //if adjacent attach
                if (closestBlock.getCoordinates().isAdjacent()) {
                    for (Direction direction : Direction.allDirections()) {
                        if (direction.getNextCoordinate().equals(closestBlock.getCoordinates())) {
                            return new Attach(direction);
                        }
                    }
                } else {
                    //move to next block
                    return AStar.astarNextStepWithAgents(closestBlock.getCoordinates(), perceptionAndMemory).orElse(new Skip());
                }
            }
        }

        if (!perceptionAndMemory.getDispensers().isEmpty()) {
            //Determine closest dispenser
            Block closestDispenser = perceptionAndMemory.getDispensers().get(0);
            for (Block dispenser : perceptionAndMemory.getDispensers()) {
                if (dispenser.getCoordinates().manhattanDistanceTo(new Point(0, 0)) < closestDispenser.getCoordinates().manhattanDistanceTo(new Point(0, 0))) {
                    if (this.lastDispenserMovedTo == null) {
                        closestDispenser = dispenser;
                    } else {
                        //check if last dispenser
                        if (dispenser.getCoordinates().manhattanDistanceTo(lastDispenserMovedTo.getCoordinates()) < closestDispenser.getCoordinates().manhattanDistanceTo(lastDispenserMovedTo.getCoordinates())) {
                            closestDispenser = dispenser;
                        }
                    }
                }
            }
            lastDispenserMovedTo = closestDispenser;
            //if adjacent attach
            if (closestDispenser.getCoordinates().isAdjacent()) {
                for (Direction direction : Direction.allDirections()) {
                    if (direction.getNextCoordinate().equals(closestDispenser.getCoordinates())) {
                        return new Request(direction);
                    }
                }
            } else {
                //move to next dispenser
                Direction direction = Direction.WEST;
                for (Direction d : Direction.allDirections()) {
                    if (d.getNextCoordinate().manhattanDistanceTo(closestDispenser.getCoordinates()) < direction.getNextCoordinate().manhattanDistanceTo(closestDispenser.getCoordinates())) {
                        direction = d;
                    }
                }
                return moveTo(direction);
            }
        }

        return new Skip();
    }

    @Nullable
    private Clear IfRotateFailedBreakFree() {
        if(perceptionAndMemory.getLastAction() != null){
            if(perceptionAndMemory.getLastAction().getName().equals("rotate")
                    && !perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")
                    && perceptionAndMemory.getEnergy() > 30){
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
            }
        }
        return null;
    }

    private boolean checkIfNotCloseToOtherAgent(Block block) {
        for (Point agentPosition : perceptionAndMemory.getFriendlyAgents()) {
            if (agentPosition.isAdjacentTo(block.getCoordinates()) && !agentPosition.equals(new Point(0, 0))) {
                return false;
            }
        }
        return true;
    }

    private G6Action moveTo(Direction direction) {

        for (Block attachedBlock : perceptionAndMemory.getDirectlyAttachedBlocks()) {

            if (!attachedBlock.getCoordinates().invert().equals(direction.getNextCoordinate())) {
                for (Point obstacle : perceptionAndMemory.getObstacles()) {
                    if (obstacle.equals(direction.rotate(Rotation.CLOCKWISE).getNextCoordinate()) || obstacle.equals(direction.getNextCoordinate().invert())) {
                        return new Clear(obstacle);
                    }
                }
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        for (Point obstacle : perceptionAndMemory.getObstacles()) {
            if (direction.getNextCoordinate().equals(obstacle)) {
                return new Clear(obstacle);
            }
        }
        return new Move(direction);
    }

    @Override
    public boolean isSucceding() {
        //is Succeding if the Agent knows the position of an Dispenser or Block
        //can be improved by checking tasks
        return (!perceptionAndMemory.getBlocks().isEmpty() || !perceptionAndMemory.getDispensers().isEmpty());
    }

    @Override
    public boolean isFullfilled() {
        return !perceptionAndMemory.getDirectlyAttachedBlocks().isEmpty();
    }

    @Override
    public String getName() {
        return "G6GoalRetrieveBlock";
    }

    @Override
    public boolean preconditionsMet() {
        //unclear if can attach
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        //no dispensers known
        if (perceptionAndMemory.getDispensers().isEmpty()) return false;
        // true if can attach block with current role
        return canAttach();
    }

    private boolean canAttach() {
        return perceptionAndMemory.getCurrentRole()
                .getPossibleActions()
                .stream()
                .anyMatch(action -> action.equals("attach"));
    }
}
