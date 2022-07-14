package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class G6GoalFulfillSingleTaskV1 extends GoalWithTask implements Goal{

    private final PerceptionAndMemory perceptionAndMemory;

    private List<G6Action> actionQueque;

    public G6GoalFulfillSingleTaskV1(PerceptionAndMemory perceptionAndMemory, String taskName) {
        super(perceptionAndMemory, taskName, 0);
        this.perceptionAndMemory = perceptionAndMemory;
        this.actionQueque = new LinkedList<>();
    }

    @Override
    public G6Action getNextAction() {

        if (!actionQueque.isEmpty()
                && perceptionAndMemory.getLastAction() != null
                && perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")) {
            return actionQueque.remove(0);
        }
        Task task = getTask();
        Block block = task.getRequirements().get(0);

        //If matching Block is not attached move to dispenser and attach Block
        if(perceptionAndMemory.getDirectlyAttachedBlocks()
                .stream()
                .noneMatch(b -> b.getBlocktype().equals(block.getBlocktype()))){
            return moveToDispenserAndAttachBlock(block);

        //else if not in Goalzone move to Goalzone
        } else if (perceptionAndMemory.getGoalZones().stream().noneMatch(goalzone -> goalzone.equals(new Point(0,0)))){
            return moveToClosestGoalZone();

            // else (has matching Block attached and is in Goalzone) submit Task
        } else {
            if (perceptionAndMemory.getDirectlyAttachedBlocks().stream()
                    .anyMatch(x -> x.getCoordinates().equals(block.getCoordinates()) && x.getBlocktype().equals(block.getBlocktype()))){
                return new Submit(task);
            } else {
                return rotateAndSubmit(block);
            }
        }
    }

    @NotNull
    private G6Action rotateAndSubmit(Block block) {
        //rotate block to match task
        Block attachedBlock = perceptionAndMemory.getDirectlyAttachedBlocks()
                .stream()
                .filter(x-> x.getBlocktype().equals(block.getBlocktype()))
                .findFirst()
                .orElseThrow();

        //rotation clockwise
        if (attachedBlock.getCoordinates().rotate(Rotation.CLOCKWISE).equals(block.getCoordinates())){
            G6Action rotation = new Rotate(Rotation.CLOCKWISE);
            return rotateOrClear(block, rotation);
        }
        // rotation counterclockwise
        else if (attachedBlock.getCoordinates().rotate(Rotation.COUNTERCLOCKWISE).equals(block.getCoordinates())){
            G6Action rotation = new Rotate(Rotation.COUNTERCLOCKWISE);
            return rotateOrClear(block, rotation);
        }
        //is opposite
        else {
            G6Action rotationClockwise = new Rotate(Rotation.CLOCKWISE);
            G6Action rotationCounterclockwise = new Rotate(Rotation.COUNTERCLOCKWISE);
            if (rotationClockwise.predictSuccess(perceptionAndMemory)) return rotationClockwise;
            if(rotationCounterclockwise.predictSuccess(perceptionAndMemory)) return rotationCounterclockwise;
            return new Clear(block.getCoordinates().rotate(Rotation.COUNTERCLOCKWISE));
        }
    }

    @NotNull
    private G6Action rotateOrClear(Block block, G6Action rotation) {
        if (rotation.predictSuccess(perceptionAndMemory)){
            return rotation;
        } else {
            return new Clear(block.getCoordinates());
        }
    }

    private G6Action moveToClosestGoalZone() {
        Point closestGoalZone = perceptionAndMemory.getGoalZones().stream().min(Comparator.comparingInt(a-> a.manhattanDistanceTo(new Point(0,0)))).orElseThrow() ;
        actionQueque = AStar.astarShortestPath(closestGoalZone, perceptionAndMemory);
        return actionQueque.remove(0);
    }

    private G6Action moveToDispenserAndAttachBlock(Block block) {
        List<Block> matchingDispensers = perceptionAndMemory.getDispensers().stream().filter(dispenser -> dispenser.getBlocktype().equals(block.getBlocktype())).toList();
        if(matchingDispensers.isEmpty()) return new Skip();
        Block closestDispenser = matchingDispensers
                .stream()
                .min(Comparator.comparingInt(a -> a.getCoordinates().manhattanDistanceTo(new Point(0, 0))))
                .orElseThrow();

        if (closestDispenser.getCoordinates().isAdjacent()){
            LastActionMemory lastAction = perceptionAndMemory.getLastAction();
            boolean hasBlockOnTopOfDispenser = perceptionAndMemory.getBlocks()
                    .stream()
                    .anyMatch(b -> b.getCoordinates().equals(closestDispenser.getCoordinates()));
            if (lastAction != null && lastAction.getName().equals("request") && lastAction.getSuccessMessage().equals("success") && hasBlockOnTopOfDispenser){
                return new Attach(Direction.fromAdjacentPoint(closestDispenser.getCoordinates()));
            }
            return new Request(Direction.fromAdjacentPoint(closestDispenser.getCoordinates()));
        } else {
            actionQueque = AStar.astarShortestPath(closestDispenser.getCoordinates(), perceptionAndMemory);
            return actionQueque.remove(0);
        }
    }

    @Override
    public boolean isSucceding() {
        //No Active Tasks known
        if (perceptionAndMemory.getActiveTasks() == null) return false;
        //Task no longer Active
        if (perceptionAndMemory.getActiveTasks().stream().noneMatch(task -> task.getName().equals(this.taskname))) return false;
        if (perceptionAndMemory.getGoalZones().isEmpty()) return false;
        //
       return determineIfHasBlocksAttachedOrKnowsDispenser();
    }


    private boolean determineIfHasBlocksAttachedOrKnowsDispenser() {
        Task task = getTask();
        if (task == null) return false;
        Block taskBlock  = task.getRequirements().get(0);
        boolean hasBlockAttached = perceptionAndMemory.getDirectlyAttachedBlocks()
                .stream()
                .anyMatch(block -> block.equals(taskBlock));

        boolean knowsDispenser = perceptionAndMemory.getDispensers()
                .stream()
                .anyMatch(dispenser -> dispenser.equals(taskBlock));

        return  (hasBlockAttached || knowsDispenser);
    }

    @Override
    public boolean isFullfilled() {
        if (perceptionAndMemory.getActiveTasks() == null) return true;
        //Task no longer Active
        return (perceptionAndMemory.getActiveTasks().stream().noneMatch(task -> task.getName().equals(this.taskname)));
    }

    @Override
    public String getName() {
        return "G6GoalFullfillSingleTaskv1";
    }

    @Override
    public boolean preconditionsMet() {
        Role currentRole =perceptionAndMemory.getCurrentRole();
        if (currentRole == null) return false;
        if (!(currentRole.canPerformAction("request") && currentRole.canPerformAction("submit"))){
            return false;
        }
        if (perceptionAndMemory.getGoalZones().isEmpty()) return false;
        return determineIfHasBlocksAttachedOrKnowsDispenser();
    }

    @Override
    public float getPriorityOffset() {
        Task task = getTask();
        if(task == null) return (float) -Integer.MAX_VALUE;
        Block taskBlock  = task.getRequirements().get(0);
        boolean hasBlockAttached = perceptionAndMemory.getDirectlyAttachedBlocks()
                .stream()
                .anyMatch(block -> block.equals(taskBlock));
        //case has Block already attached
        if (hasBlockAttached) return (((float) task.getReward()+1.0f) / 1000.0f);

        Block closestDispenser = determineClosestMatchingDispenser(taskBlock);
        if (closestDispenser == null) return (float) -Integer.MAX_VALUE;
        //case knows dispenser to fetch block
        return ((float)task.getReward() - ((float)closestDispenser.getCoordinates().manhattanDistanceTo(new Point(0,0 )) /10.0f) ) /1000.0f;
    }

    @Nullable
    private Block determineClosestMatchingDispenser(Block taskBlock) {
        List<Block> dispensers = perceptionAndMemory.getDispensers()
                .stream()
                .filter(dispenser -> dispenser.equals(taskBlock)).toList();
        if (dispensers.isEmpty()) return null;
        Block closestDispenser = dispensers.get(0);
        if (closestDispenser == null) return null;
        for (Block dispenser: dispensers) {
                if (dispenser.getCoordinates().manhattanDistanceTo(new Point(0,0))
                    < dispenser.getCoordinates().manhattanDistanceTo(new Point(0,0))){
                    closestDispenser = dispenser;
                }
        }
        return closestDispenser;
    }

    @Nullable
    private Task getTask() {
        Task task = null;
        for (var t : perceptionAndMemory.getActiveTasks()){
            if (t.getName().equals(taskname)) {
                task = t;
                break;
            }
        }
        return task;
    }
}
