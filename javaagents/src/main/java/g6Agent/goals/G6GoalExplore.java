package g6Agent.goals;

import g6Agent.actions.G6Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class G6GoalExplore implements Goal {
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalExplore(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        return null;
    }

    @Override
    public boolean isSucceding() {
        return true;
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalExplore";
    }
}
