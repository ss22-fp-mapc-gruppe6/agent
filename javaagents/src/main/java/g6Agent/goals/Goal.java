package g6Agent.goals;

import g6Agent.actions.G6Action;

/**
 * Interface for Goals, which represent the desires and produce the intentions of an agent.
 */
public interface Goal {
    /**
     * @return the next Action to be taken to achieve this Goal
     */
    G6Action getNextAction();

    /**
     * Is this Goal still succeding with the current beliefs
     *
     * @return is it succeding?
     */
    boolean isSucceding();

    /**
     * The Goal is reached succesfully
     *
     * @return is the goal reached?
     */
    boolean isFullfilled();

    //probably nonsense, should be decided outside and not hard coded
    //int getPriority()

    /**
     * The Name of the Goal as String for Communication and other purposes
     *
     * @return the Name as String
     */
    String getName();

}
