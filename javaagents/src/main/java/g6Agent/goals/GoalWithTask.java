package g6Agent.goals;

import g6Agent.goals.interfaces.GoalWithPriorityOffset;
import g6Agent.goals.interfaces.PingReceiver;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;


/**
 * Abstract Class for Goals which involve accepting a Task or part of a Task
 */
public abstract class GoalWithTask implements Goal, PingReceiver, GoalWithPriorityOffset {

    PerceptionAndMemory perceptionAndMemory;
    String taskname;
    int acceptedBlockIndex;

    /**
     * Constructor
     * @param perceptionAndMemory our Beliefs
     * @param taskname  the name of the accepted Task
     * @param acceptedBlockIndex The index in Task.getRequirements() of the Block the agent wants to deliver
     */
    public GoalWithTask(PerceptionAndMemory perceptionAndMemory, String taskname, int acceptedBlockIndex) {
        this.perceptionAndMemory = perceptionAndMemory;
        this.taskname = taskname;
        this.acceptedBlockIndex = acceptedBlockIndex;
    }

    /**
     *
     * @return the name of the accepted Task
     */
    public String getTaskname() {
        return taskname;
    }

    /**
     *
     * @return The index in Task.getRequirements() of the Block the agent wants to deliver
     */
    public int getAcceptedBlockIndex() {
        return acceptedBlockIndex;
    }



}
