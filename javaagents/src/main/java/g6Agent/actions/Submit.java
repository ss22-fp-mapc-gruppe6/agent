package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class Submit extends Action implements G6Action {
    /**
     * Submit the pattern of things that are attached to the agent to complete a task.
     *
     * @param task the active Task
     */
    private Task task;
    public Submit(Task task) {
        super("submit", new Identifier(task.getName()));
        this.task = task;
    }
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) throws Exception {
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        boolean isTask = perceptionAndMemory.getAllTasks().stream().anyMatch((x -> x.equals(task)));
        return  isTask;
    }
}
