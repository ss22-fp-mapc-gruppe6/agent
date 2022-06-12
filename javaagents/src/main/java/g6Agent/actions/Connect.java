package g6Agent.actions;

import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;

// Two agents can connect blocks attached to them.
public class Connect implements g6Action{

    private PerceptionAndMemory perception;
    private final Point pointTo;
    private final MyTestAgent partner;
    private final Point pointFrom;
    private final String partnerName;


    public Connect(MyTestAgent partner,  Point pointTo, Point pointFrom) {
        this.pointFrom = pointFrom;
        this.pointTo = pointTo;
        partnerName = partner.getName();
        this.partner = partner;
    }

    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
        this.perception = perception;
        if (perception.getLastAction().getSuccessMessage().equals("success")){
           // Point agentPosition = agent.getPosition(step);
            perception.getLastAction().setSuccessfulMessage("success");
            perception.getLastAction().setName("connect");
        }
    }
}
