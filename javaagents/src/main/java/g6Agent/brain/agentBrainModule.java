package g6Agent.brain;

import java.util.HashMap;

public class agentBrainModule {
    HashMap<Integer, Object> agentBrain = new HashMap<>();

    public void addData(Integer round, Object data) {
        agentBrain.put(round, data);
    }

    public HashMap<Integer, Object> getData() {
        return agentBrain;
    }
}
