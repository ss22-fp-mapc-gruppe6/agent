package g6Agent.goals;

import g6Agent.actions.G6Action;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class G6GoalFullfillSingleTaskv1 extends GoalWithTask implements Goal{

    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalFullfillSingleTaskv1(PerceptionAndMemory perceptionAndMemory, String taskName) {
        super(perceptionAndMemory, taskName, 0);
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        return null;
    }

    @Override
    public boolean isSucceding() {
        //No Active Tasks known
        if (perceptionAndMemory.getActiveTasks() == null) return false;
        //Task no longer Active
        if (perceptionAndMemory.getActiveTasks().stream().noneMatch(task -> task.getName().equals(this.taskname))) return false;
        //
       return determineIfHasBlocksAttachedOrKnowsDispenser();


    }


    private Boolean determineIfHasBlocksAttachedOrKnowsDispenser() {
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
