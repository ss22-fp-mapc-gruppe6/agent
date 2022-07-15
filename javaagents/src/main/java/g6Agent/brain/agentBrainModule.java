package g6Agent.brain;

import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

import java.util.HashMap;

public class agentBrainModule {
    HashMap<Integer, PerceptionAndMemory> agentBrain = new HashMap<Integer, PerceptionAndMemory>();

    public agentBrainModule() {
    }

    public void addData(PerceptionAndMemory perceptionAndMemory) {
        if (perceptionAndMemory.getLastId() >= -1) {
            agentBrain.put(perceptionAndMemory.getCurrentStep(), perceptionAndMemory);
        }
    }

    public HashMap<Integer, PerceptionAndMemory> getData() {
        return agentBrain;
    }
}
