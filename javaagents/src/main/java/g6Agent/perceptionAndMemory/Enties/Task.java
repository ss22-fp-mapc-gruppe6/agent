package g6Agent.perceptionAndMemory.Enties;

import eis.iilang.*;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Class To Save Tasks
 */
public class Task {
    private final String name;
    private final int start, reward;
    private final List<Block> requirements;

    public Task(Percept p){
        name = ((Identifier)p.getParameters().get(0)).toProlog();
        start = ((Numeral) p.getParameters().get(1)).getValue().intValue();
        reward = ((Numeral) p.getParameters().get(2)).getValue().intValue();
        requirements = translateRequirements((ParameterList) p.getParameters().get(3));
    }

    private List<Block> translateRequirements(ParameterList parameters) {
    List<Block> list = new ArrayList<>(parameters.size());
        for (Parameter p: parameters) {
            int x = ((Numeral) ((Function)p).getParameters().get(0)).getValue().intValue();
            if (x>5) x = -1; //Hack for Modulo Mapsize
            int y = ((Numeral) ((Function)p).getParameters().get(1)).getValue().intValue();
                if (y>5) y = -1; //Hack for Modulo Mapsize
            Block requirement = new Block(new Point(x,y), ((Identifier)((Function) p).getParameters().get(2)).toProlog());
            list.add(requirement);
        }
    return list;
    }

    /**
     * @return The Name of the Task
     */
    public String getName() {
        return name;
    }

    /**
     * @return the first step during which the task can be completed
     */
    public int getStart() {
        return start;
    }

    /**
     *
     * @return The Reward of the task
     */
    public int getReward() {
        return reward;
    }

    public List<Block> getRequirements() {
        return requirements;
    }

    public String toString(){

        return
                "Name :       " + this.name + "\n" +
                "Reqirements :   " + this.requirements + "\n" +
                "Reward      :   " +  this.reward +"\n";
    }
}
