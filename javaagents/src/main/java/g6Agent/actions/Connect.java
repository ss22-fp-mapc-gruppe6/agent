package g6Agent.actions;

import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Direction;
import g6Agent.services.Point;

// Two agents can connect blocks attached to them.
public class Connect implements g6Action{

    private final Direction direction;
    private PerceptionAndMemory perception;
    /*private final Block blockA;
    private final MyTestAgent agentA;
    private final Block blockB;
    private final MyTestAgent agentB;**/

    public Connect(Direction direction, String name) {
        super();
        this.direction = direction;
        perception = new PerceptionAndMemoryImplementation();
    }
    @Override
    public void setSucceededEffect(MyTestAgent agent, int step) {
        if (perception.getLastAction().getSuccessMessage().equals("success")){
            Point agentPosition = agent.getPosition(step);
            //connect(agent, direction);
            //connect(agent)
        }
    }
}
