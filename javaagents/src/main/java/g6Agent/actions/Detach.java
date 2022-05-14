package g6Agent.actions;

import g6Agent.actions.Objects.Pair_BlockAgent;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.HashSet;
//Detaches block from the agent.
public class Detach implements g6Action {
    private final Direction direction;
    private PerceptionAndMemory perception;

    private  HashSet<Pair_BlockAgent> detachedList;

    public Detach(Direction direction, String name) {
        super();
        this.direction = direction;
        perception = new PerceptionAndMemoryImplementation();
    }

    public Direction getDirection() { return direction;}

    public HashSet getDetachedList() {return this.detachedList;}

    public void setSucceededEffect(MyTestAgent agent, int step) {
        if (perception.getLastAction().getSuccessMessage().equals("success")) {
           detachBlockFromAgent(step,  agent);
        }
    }
    public void detachBlockFromAgent(int step,  MyTestAgent agent) {

        boolean remove = false;
        Block block;
        Point newBlockPosition;
        Pair_BlockAgent pair;
        HashSet<Pair_BlockAgent> attachedList;

        attachedList = agent.getAttachedList();
        pair = attachedList.stream().filter(t -> t.getAgent().equals(agent)).findFirst().get();
        if(attachedList != null) {
            remove = attachedList.remove(pair);
            agent.setAttachedList(attachedList);
        }
        if (remove) {
            if(detachedList.isEmpty()) detachedList = new HashSet<>();
            detachedList.add(pair);
            block = pair.getBlock();
            block.setIsAttached(false);
            Point direction = this.direction.getNextCoordinate();
            newBlockPosition = new Point(agent.getPosition(step).x + direction.x, agent.getPosition(step).y + direction.y);
            block.setCoordinates(newBlockPosition);
        }
    }

}
