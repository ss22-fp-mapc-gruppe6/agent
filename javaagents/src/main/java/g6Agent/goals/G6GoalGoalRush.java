package g6Agent.goals;

import g6Agent.actions.g6Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class G6GoalGoalRush implements Goal {

    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalGoalRush(PerceptionAndMemory perceptionAndMemory) {
    this.perceptionAndMemory = perceptionAndMemory;
    }


    @Override
    public g6Action getNextAction() {

        //SubGoal : find Goal Zone
        //Subgoal : build Task together with other agents


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
        return "G6GoalGoalRush";
    }
}
