package g6Agent.decisionModule.configurations;

import g6Agent.decisionModule.entities.Strategy;
import g6Agent.goals.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

import java.util.List;

public class TSDMUpdatedConfig implements DecisionModuleConfiguration{

    @Override
    public List<Goal> generateGoals(PerceptionAndMemory perceptionAndMemory, Strategy strategy) {
        return List.of(
                new G6GoalExplore(perceptionAndMemory),
                new G6GoalChangeRole(strategy.getPreferredRoleName(), perceptionAndMemory),
                new G6GoalRetrieveBlockV2(perceptionAndMemory),
                new G6GoalGoalRushV2(perceptionAndMemory)
        );
    }

    @Override
    public double priority(Goal goal, Strategy strategy) {
        if (strategy.equals(Strategy.OFFENSE)) {
            if (goal instanceof G6GoalExplore) return 0.0;
            if (goal instanceof G6GoalChangeRole) return 1.0;
            if (goal instanceof G6GoalRetrieveBlockV2) return 2.0;
            if (goal instanceof G6GoalGoalRushV2) return 3.0;
        }
        return -1;
    }

    @Override
    public double getMaxOffensivePercentage() {
        return 1.0;
    }
}
