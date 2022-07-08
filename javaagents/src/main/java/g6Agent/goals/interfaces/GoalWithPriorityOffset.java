package g6Agent.goals.interfaces;

/**
 * Defines a goal with an Priority offset, which is important, if more than one instance of this Goal is created in the
 * possible goals to pick.
 */

public interface GoalWithPriorityOffset {


    /**
     * Returns an Offset which is a number < 1 , which determines how important this task is in relation to others
     * of the same instance. For Example the points of the chosen Task in relation to the number of blocks needed.
     *
     * @return the Offset
     */
    float getPriorityOffset();
}
