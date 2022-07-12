package g6Agent.goals;

import g6Agent.actions.G6Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class G6GoalDefendGoalZone implements Goal {
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalDefendGoalZone(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }


    @Override
    public G6Action getNextAction() {
        return null;
    }

    @Override
    public boolean isSucceding() {
        return preconditionsMet();
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalDefendGoalZone";
    }

    @Override
    public boolean preconditionsMet() {
        if (perceptionAndMemory.getGoalZones().isEmpty()) return false;
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        return perceptionAndMemory.getCurrentRole().canPerformAction("clear");
    }

}
