package g6Agent.perceptionAndMemory.Enties;

import eis.iilang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

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
        this.possibleActions = (possibleActions == null ? new ArrayList<>() : possibleActions);
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

    public static Role from(Percept percept) {
        if (!(percept.getParameters().size() == 6 || percept.getParameters().size() == 1))
            throw new IllegalArgumentException("Role with unforeseen parameter size : " + percept + "size :" + percept.getParameters().size());

        return new Role(
                ((Identifier) percept.getParameters().get(0)).getValue(),
                ((Numeral) percept.getParameters().get(1)).getValue().intValue(),
                StreamSupport.stream(((ParameterList) percept.getParameters().get(2)).spliterator(), false)
                        .map(s -> ((Identifier) s).getValue())
                        .toList(),
                StreamSupport.stream(((ParameterList) percept.getParameters().get(3)).spliterator(), false)
                        .map(n -> ((Numeral) n).getValue().intValue())
                        .toList(),
                ((Numeral) percept.getParameters().get(4)).getValue().doubleValue(),
                ((Numeral) percept.getParameters().get(5)).getValue().intValue()
        );
    }
}
