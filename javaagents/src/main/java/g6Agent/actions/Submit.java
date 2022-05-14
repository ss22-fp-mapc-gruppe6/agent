package g6Agent.actions;

import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.PerceptionAndMemory;

public class Submit implements  g6Action{
    private String taskName;
    private PerceptionAndMemory perception;

    @Override
    public void setSucceededEffect(MyTestAgent agent, int step) {
        if (perception.getLastAction().getSuccessMessage().equals("success")) {
           // Task task = agent.getTask().getName();
        }
    }
}
