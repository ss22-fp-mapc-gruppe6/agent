package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.actions.Objects.Pair_BlockAgent;
import g6Agent.agents.MyTestAgent;
import g6Agent.environment.CellObject;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.HashSet;

// Attaches a block to the agent.
public class Attach extends Action implements g6Action{
    private static final String TAG = "Attach";
    private final Direction direction;
    private PerceptionAndMemory perception;

    private  HashSet<Pair_BlockAgent> attachedList;



    public Attach(Direction direction, String name) {
        super(name);
        this.direction = direction;
        perception = new PerceptionAndMemoryImplementation();
    }

    public Direction getDirection() {
        return direction;}

    public  HashSet getAttachedList() {return this.attachedList;}

    @Override
    public void setSucceededEffect(MyTestAgent agent, int step) {
        if (perception.getLastAction().getSuccessMessage().equals("success")) {
            Point direction = this.direction.getNextCoordinate();
            Point agentPosition = agent.getPosition(step);
            Point blockPosition = new Point(agentPosition.x + direction.x, agentPosition.y + direction.y );
            Block block = new Block(blockPosition, "block_B1");
            attachBlockToAgent(block, agent);


        }
    }

    public  void attachBlockToAgent(Block block, MyTestAgent agent) {
        if (agent != null && !block.getIsAttached() && block != null) {
            if(attachedList.isEmpty()) attachedList = new HashSet<>();
            attachedList.add(new Pair_BlockAgent(block, agent));
            agent.setAttachedList(attachedList);
            block.setIsAttached(true);
        }

    }



}
