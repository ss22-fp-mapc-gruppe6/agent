package g6Agent.actions;


import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public interface G6Action {
    String TAG = "Action";

    boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) ;
}

