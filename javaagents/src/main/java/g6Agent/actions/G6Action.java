package g6Agent.actions;


import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public interface G6Action {
    static final String TAG = "Action";

    boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) throws Exception;
}

