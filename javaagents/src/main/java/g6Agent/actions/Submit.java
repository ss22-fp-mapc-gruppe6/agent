package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class Submit extends Action implements G6Action {
    private final Task task;


    /**
     * Submit the pattern of things that are attached to the agent to complete a task.
     *
     * @param task the active Task
     */
    public Submit(Task task) {
        super("submit", new Identifier(task.getName()));
        this.task = task;
    }
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) {
        boolean isTask = perceptionAndMemory.getAllTasks().stream().anyMatch((x -> x.equals(task)));
        boolean hasAllBlocksAtRightPosition = true;
        for (Block b : task.getRequirements()){
            if (perceptionAndMemory.getDirectlyAttachedBlocks()
                    .stream()
                    .noneMatch(x -> x.getCoordinates().equals(b.getCoordinates()) && x.getBlocktype().equals(b.getBlocktype()))
            ) hasAllBlocksAtRightPosition = false;
        }
        return  isTask &&hasAllBlocksAtRightPosition;
    }
}
