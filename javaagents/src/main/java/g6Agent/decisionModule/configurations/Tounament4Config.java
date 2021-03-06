package g6Agent.decisionModule.configurations;

import g6Agent.decisionModule.entities.Strategy;
import g6Agent.goals.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

import java.util.ArrayList;
import java.util.List;

public class Tounament4Config implements DecisionModuleConfiguration{
    @Override
    public List<Goal> generateGoals(PerceptionAndMemory perceptionAndMemory, Strategy strategy) {

        List<Goal> goals = new ArrayList<>();
        goals.add(new G6GoalExploreV2(perceptionAndMemory));
        goals.add(new G6GoalChangeRole(strategy.getPreferredRoleName(), perceptionAndMemory));
        goals.add(new G6GoalDefendGoalZone(perceptionAndMemory));
        goals.add(new G6GoalRetrieveBlockV2(perceptionAndMemory));
        /*
        for (Task task : perceptionAndMemory.getActiveTasks()){
            goals.add(new G6GoalFulfillSingleTaskV1(perceptionAndMemory, task.getName()));
        }

         */
        goals.add(new G6GoalGoalRushV2(perceptionAndMemory));
        return goals;
    }

    @Override
    public double priority(Goal goal, Strategy strategy) {
        switch (strategy){
            case OFFENSE -> {
                if (goal instanceof G6GoalExploreV2) return 0.0;
                if (goal instanceof G6GoalChangeRole) return 1.0;
                if (goal instanceof G6GoalRetrieveBlockV2) return 2.0;
                if (goal instanceof G6GoalGoalRushV2) return 3.0;
                //if (goal instanceof G6GoalFulfillSingleTaskV1) return 2.0;
                return -1;
            }
            case DEFENSE -> {
                if (goal instanceof G6GoalExploreV2) return 0.0;
                if (goal instanceof G6GoalDefendGoalZone) return 1.0;
                if (goal instanceof G6GoalChangeRole) return 2.0;
                return -1;
            }
        }
        return -1;
    }

    @Override
    public double getMaxOffensivePercentage() {
        return 0.9;
    }
}
