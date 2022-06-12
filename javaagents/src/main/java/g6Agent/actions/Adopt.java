package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

import java.util.List;

public class Adopt extends Action implements g6Action{

    private PerceptionAndMemory perception;
    private String roleName;

    public Adopt(String roleName, MyTestAgent agent){
        super("adopt");
        this.roleName = roleName;
    }
    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
        this.perception = perception;
        List<Point> roleZones = perception.getRoleZones();
if(roleZones.contains(agent.getPosition(step)))
agent.setRoleName(roleName);
        perception.getLastAction().setSuccessfulMessage("success");
        perception.getLastAction().setName("adopt");
    }
}
