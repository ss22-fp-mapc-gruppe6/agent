package g6Agent.goals;

import g6Agent.actions.G6Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class G6GoalRetrieveBlock implements Goal {
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalRetrieveBlock(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        //subgoal : find dispenser for a block required of a task
        //subgoal : go to dispenser and retrieve block from it

        return null;
    }

    @Override
    public boolean isSucceding() {
        return false;
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalRetrieveBlock";
    }
}
