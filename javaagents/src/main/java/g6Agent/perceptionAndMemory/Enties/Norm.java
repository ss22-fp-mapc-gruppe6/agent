package g6Agent.perceptionAndMemory.Enties;

import eis.iilang.*;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Class to represent a Norm
 */

public class Norm {
    private final String name;
    private final int start;
    private final int end;
    private final List<Function> requirements;
    private final int level;

    public Norm(String name, int start, int end, List<Function> requirements, int level) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.requirements = requirements;
        this.level = level;
    }

    /**
     * @return the Name of the Norm
     */
    public String getName() {
        return name;
    }

    /**
     * @return  the step in which a norm becomes active
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the step in which a norm becomes inactive
     */
    public int getEnd() {
        return end;
    }

    /**
     * TODO find a better return type
     * @return the Requirements of the Norm.
     */
    public List<Function> getRequirements() {
        return requirements;
    }

    /**
     * INTEGER VALUE NOT UNDERSTOOD TODO
     *
     * @return whether the norm applies to individual agents or a team of agents
     */
    public int getLevel() {
        return level;
    }

    public static Norm from(Percept percept) {
        return new Norm(
                ((Identifier) percept.getParameters().get(0)).getValue(),
                ((Numeral) percept.getParameters().get(1)).getValue().intValue(),
                ((Numeral) percept.getParameters().get(2)).getValue().intValue(),
                StreamSupport.stream(((ParameterList) percept.getParameters().get(3)).spliterator(), false)
                        .map(p -> ((Function) p))
                        .toList(),
                ((Numeral) percept.getParameters().get(4)).getValue().intValue()
        );
    }
}
