package g6Agent.actions;

import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;

public class Submit implements  g6Action{
    private String taskName;
    private PerceptionAndMemory perception;
    public Submit(String taskName ) {
        this.taskName = taskName;

    }

    @Override
    public void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception) {
        if (perception.getLastAction().getSuccessMessage().equals("success")) {
            this.perception = perception;
            perception.getLastAction().setSuccessfulMessage("success");
            perception.getLastAction().setName("submit");
           // Task task = agent.getTask().getName();
        }
    }
}
