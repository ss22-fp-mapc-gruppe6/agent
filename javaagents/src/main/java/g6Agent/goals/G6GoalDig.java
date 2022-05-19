package g6Agent.goals;

import g6Agent.actions.G6Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class G6GoalDig implements Goal {
    public G6GoalDig(PerceptionAndMemory perceptionAndMemory) {
        //subgoal: go in dig range (of current role)
        //subgoal: destroy obstacles
    }

    @Override
    public G6Action getNextAction() {
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
        return "G6GoalDig";
    }
}
