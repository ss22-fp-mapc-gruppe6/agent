package g6Agent.perceptionAndMemory.Enties;

import java.util.List;

/**
 * Class to define a Role of an Agent.
 */
public class Role {
    private final String name;
    private final int visionRange;
    private final List<String> possibleActions;
    private final List<Integer> movementSpeed;
    private final double clearActionChance;
    private final int clearActionMaximumDistance;


    public Role(String name, int visionRange, List<String> possibleActions, List<Integer> movement, double clearActionChance, int clearActionMaximumDistance) {
        this.name = name;
        this.visionRange = visionRange;
        this.possibleActions = possibleActions;
        this.movementSpeed = movement;
        this.clearActionChance = clearActionChance;
        this.clearActionMaximumDistance = clearActionMaximumDistance;
    }

    public String getName() {
        return name;
    }

    /**
     *
     * @return the Vision Range of this Role
     */
    public int getVisionRange() {
        return visionRange;
    }

    /**
     * @return A List of the names of possible Actions
     */
    public List<String> getPossibleActions() {
        return possibleActions;
    }

    /**
     * the movement speed of the Agent. With the first Entry being the Speed with no Blocks Attached,
     * the second with one Block attached ...
     * @return the movement speed of the Agent.
     */
    public List<Integer> getMovementSpeed() {
        return movementSpeed;
    }

    /**
     * @return the Chance that a clear Action succeds
     */
    public double getClearActionChance() {
        return clearActionChance;
    }

    /**
     * @return the maximum Distance to perform a clear Action
     */
    public int getClearActionMaximumDistance() {
        return clearActionMaximumDistance;
    }
}
