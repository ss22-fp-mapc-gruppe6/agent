package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import g6Agent.perceptionAndMemory.Enties.Task;

public class Submit extends Action implements G6Action {
    /**
     * Submit the pattern of things that are attached to the agent to complete a task.
     * @param task the active Task
     */
    public Submit(Task task){
        super("submit", new Identifier(task.getName()));
    }

}
