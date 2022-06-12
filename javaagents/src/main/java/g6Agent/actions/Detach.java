package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.actions.Objects.Pair_BlockAgent;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.HashSet;
//Detaches block from the agent.
public class Detach extends Action implements g6Action {
    private final Direction direction;
    private PerceptionAndMemory perception;

    private  HashSet<Pair_BlockAgent> detachedList;

    public Detach(Direction direction, String name ) {
        super("detach");
        this.direction = direction;

    }

    public Direction getDirection() { return direction;}

    public HashSet getDetachedList() {return this.detachedList;}

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

    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
        this.perception = perception;
        if (perception.getLastAction().getSuccessMessage().equals("success")) {
            detachBlockFromAgent(step,  agent);
            perception.getLastAction().setSuccessfulMessage("success");
            perception.getLastAction().setName("detach");
        }
    }
}
