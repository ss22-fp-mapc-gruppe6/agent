package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.decisionModule.manhattanDistanceMove.ManhattanDistanceMove;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static g6Agent.decisionModule.astar.AStar.astarNextStepWithAgents;

public class G6GoalRetrieveBlockV2 implements Goal{
    private final PerceptionAndMemory perceptionAndMemory;
    private Block lastDispenserMovedTo;

    public G6GoalRetrieveBlockV2(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        //anti block
        G6Action actionToBreakFree = ifStuckBreakFree();
        if (actionToBreakFree != null) return actionToBreakFree;

        if (!perceptionAndMemory.getBlocks().isEmpty()) {
            G6Action actionToPickUpBlock = moveToNextBlockAndPickItUp();
            if (actionToPickUpBlock != null) return actionToPickUpBlock;
        }
        if (!perceptionAndMemory.getDispensers().isEmpty()) {
            return moveToClosestDispenserAndRequestBlock();
        }
        return new Skip();
    }

    private G6Action ifStuckBreakFree() {
        LastActionMemory lastAction = perceptionAndMemory.getLastAction();
        if (lastAction.getName().equals("move") && !lastAction.getSuccessMessage().equals("success")){
            List<Point> adjacentObstacles = perceptionAndMemory.getObstacles().stream().filter(Point::isAdjacent).toList();
            if(!adjacentObstacles.isEmpty()){
                return new Clear(adjacentObstacles.get(0));
            }
            List<Move> possibleMoves = Arrays.stream(Direction.allDirections()).map(direction -> new Move(direction)).filter(move -> move.predictSuccess(perceptionAndMemory)).toList();
            if (!possibleMoves.isEmpty()){
                return possibleMoves.stream().findFirst().orElseThrow();
            }
        }
        return null;
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
            int distanceToDispenser = closestDispenser.getCoordinates().manhattanDistanceTo(new Point(0,0));
            if (distanceToDispenser == 2){
                if (perceptionAndMemory.getFriendlyAgents().stream().anyMatch(agent -> agent.isAdjacentTo(closestDispenser.getCoordinates()))){
                    return new Skip();
                }
            }
            //move to next dispenser
            Point closestPointAdjacentToDispenser = Arrays
                    .stream(Direction.allDirections())
                    .map(direction -> direction.getNextCoordinate().add(closestDispenser.getCoordinates()))
                    .min(Comparator.comparingInt(o -> o.manhattanDistanceTo(new Point(0, 0)))).orElseThrow();
            G6Action a = astarNextStepWithAgents(closestPointAdjacentToDispenser, perceptionAndMemory).orElse(new Skip());
            if (a instanceof Move move && !move.predictSuccess(perceptionAndMemory)){
                return AStar.astarNextStepWithAgents(closestPointAdjacentToDispenser, perceptionAndMemory)
                        .orElse(ManhattanDistanceMove.nextAction(closestPointAdjacentToDispenser, perceptionAndMemory));
            }
            return astarNextStepWithAgents(closestPointAdjacentToDispenser, perceptionAndMemory)
                    .orElse(ManhattanDistanceMove.nextAction(closestPointAdjacentToDispenser, perceptionAndMemory));
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
        if (perceptionAndMemory.getBlocks().isEmpty()) return null;
        List<Block> unattachedBlocks = perceptionAndMemory.getBlocks()
                .stream()
                .filter(block -> {
                    return perceptionAndMemory.getAttachedBlocks()
                            .stream()
                            .noneMatch(x-> x.getCoordinates().equals(block.getCoordinates()));
                }).collect(Collectors.toList());

        if (unattachedBlocks.isEmpty()) return null;



        Block closestBlock  = unattachedBlocks
                .stream()
                .min(Comparator.comparingInt(a-> a.getCoordinates().manhattanDistanceTo(new Point(0,0))))
                .orElseThrow();
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
            G6Action moveToNextBlock = astarNextStepWithAgents(closestBlock.getCoordinates(), perceptionAndMemory)
                    .orElse(ManhattanDistanceMove.nextAction(closestBlock.getCoordinates(), perceptionAndMemory));
            if (moveToNextBlock.predictSuccess(perceptionAndMemory)) return moveToNextBlock;
            return astarNextStepWithAgents(closestBlock.getCoordinates(), perceptionAndMemory)
                    .orElse(ManhattanDistanceMove.nextAction(closestBlock.getCoordinates(), perceptionAndMemory));
        }
        return null;
    }

    private boolean checkIfNotCloseToOtherAgent(Block block) {
        List<Point> agents = Stream.concat(perceptionAndMemory.getFriendlyAgents().stream(), perceptionAndMemory.getEnemyAgents().stream()).toList();
        for (Point agentPosition : agents) {
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
        return "G6GoalRetrieveBlockV2";
    }

    @Override
    public boolean preconditionsMet() {
        //already has a block attached
        if(!perceptionAndMemory.getDirectlyAttachedBlocks().isEmpty()) return false;
        //unclear if can attach
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        //no dispensers known
        if (perceptionAndMemory.getDispensers().isEmpty()) return false;
        // true if can attach block with current role
        return  perceptionAndMemory.getCurrentRole().canPerformAction("attach");
    }
}
