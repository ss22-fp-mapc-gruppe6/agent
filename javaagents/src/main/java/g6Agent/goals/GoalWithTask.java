package g6Agent.goals;

import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;


/**
 * Abstract Class for Goals which involve accepting a Task or part of a Task
 */
public abstract class GoalWithTask implements Goal {

    PerceptionAndMemory perceptionAndMemory;
    String taskname;
    int acceptedBlockIndex;

    /**
     * Constructor
     *
     * @param perceptionAndMemory our Beliefs
     * @param taskname            the name of the accepted Task
     * @param acceptedBlockIndex  The index in Task.getRequirements() of the Block the agent wants to deliver
     */
    public GoalWithTask(PerceptionAndMemory perceptionAndMemory, String taskname, int acceptedBlockIndex) {
        this.perceptionAndMemory = perceptionAndMemory;
        this.taskname = taskname;
        this.acceptedBlockIndex = acceptedBlockIndex;
    }

    /**
     * @return the name of the accepted Task
     */
    public String getTaskname() {
        return taskname;
    }

    /**
     * @return The index in Task.getRequirements() of the Block the agent wants to deliver
     */
    public int getAcceptedBlockIndex() {
        return acceptedBlockIndex;
    }

    /**
     * @param ping can set a point for communication purposes, for example a meeting point
     */
    public abstract void setPing(Point ping);

    /**
     * Returns an Offset which is a number < 1 , which determines how important this task is in relation to others
     * of the same instance. For Example the points of the chosen Task in relation to the number of blocks needed.
     *
     * @return the Offset
     */
    public abstract float getPriorityOffset();

}
