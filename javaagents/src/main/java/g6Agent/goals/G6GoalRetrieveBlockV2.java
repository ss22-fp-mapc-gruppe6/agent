package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

import static g6Agent.decisionModule.astar.AStar.astarNextStep;

public class G6GoalRetrieveBlockV2 implements Goal{
    private final PerceptionAndMemory perceptionAndMemory;
    private Block lastDispenserMovedTo;

    public G6GoalRetrieveBlockV2(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        if (!perceptionAndMemory.getBlocks().isEmpty()) {
            G6Action actionToPickUpBlock = moveToNextBlockAndPickItUp();
            if (actionToPickUpBlock != null) return actionToPickUpBlock;
        }
        if (!perceptionAndMemory.getDispensers().isEmpty()) {
            return moveToClosestDispenserAndRequestBlock();
        }
        return new Skip();
    }

    private G6Action moveToClosestDispenserAndRequestBlock() {
        //Determine closest dispenser
        Block closestDispenser = determineClosestDispenser();
        lastDispenserMovedTo = closestDispenser;
        //if adjacent request
        if (closestDispenser.getCoordinates().isAdjacent()) {
            return new Request(Direction.fromAdjacentPoint(closestDispenser.getCoordinates()));
        } else {
            //If friendly Agent is at Dispenser wait
            if (closestDispenser.getCoordinates().manhattanDistanceTo(new Point(0,0)) == 2){
                if (perceptionAndMemory.getFriendlyAgents().stream().anyMatch(agent -> agent.isAdjacentTo(closestDispenser.getCoordinates()))){
                    return new Skip();
                }
            }
            //move to next dispenser
            return AStar.astarNextStep(closestDispenser.getCoordinates(), perceptionAndMemory).orElse(new Skip());
        }
    }

    private Block determineClosestDispenser() {
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
        return closestDispenser;
    }

    @Nullable
    private G6Action moveToNextBlockAndPickItUp() {
        //determine next block
        Block closestBlock = perceptionAndMemory.getBlocks()
                .stream()
                .min(Comparator.comparingInt(a-> a.getCoordinates().manhattanDistanceTo(new Point(0,0))))
                .orElseThrow() ;

        //If Adjacent Attach , if is not adjacent to other agent or is requested by self.
        if (closestBlock.getCoordinates().isAdjacent()) {
            if (checkIfNotCloseToOtherAgent(closestBlock)){
                return new Attach(Direction.fromAdjacentPoint(closestBlock.getCoordinates()));
            }
            LastActionMemory lastAction = perceptionAndMemory.getLastAction();
            if (lastAction != null && lastAction.getName().equals("request") && lastAction.getSuccessMessage().equals("success")){
                return new Attach(Direction.fromAdjacentPoint(closestBlock.getCoordinates()));
            }
        }

        List<Block> blocksNotCloseToOtherAgents = perceptionAndMemory.getBlocks().stream().filter(this::checkIfNotCloseToOtherAgent).toList();

        if (!blocksNotCloseToOtherAgents.isEmpty()){
            closestBlock = blocksNotCloseToOtherAgents
                    .stream()
                    .min(Comparator.comparingInt(a-> a.getCoordinates().manhattanDistanceTo(new Point(0,0))))
                    .orElseThrow() ;

            //move to next block
            return astarNextStep(closestBlock.getCoordinates(), perceptionAndMemory).orElseThrow();
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
        return  perceptionAndMemory.getCurrentRole().canPerformAction("attach");
    }
}
