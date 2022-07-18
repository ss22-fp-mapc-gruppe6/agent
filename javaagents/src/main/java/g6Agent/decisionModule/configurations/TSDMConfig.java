package g6Agent.decisionModule.configurations;

import g6Agent.decisionModule.entities.Strategy;
import g6Agent.goals.*;
import g6Agent.goals.old.G6GoalExplore;
import g6Agent.goals.old.G6GoalGoalRush;
import g6Agent.goals.old.G6GoalRetrieveBlock;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

import java.util.List;

/**
 * Configuration, that matches TheStupidestDecisionModule
 */

public class TSDMConfig implements DecisionModuleConfiguration {

    @Override
    public List<Goal> generateGoals(PerceptionAndMemory perceptionAndMemory, Strategy strategy) {
        return List.of(
                new G6GoalExplore(perceptionAndMemory),
                new G6GoalChangeRole(strategy.getPreferredRoleName(), perceptionAndMemory),
                new G6GoalRetrieveBlock(perceptionAndMemory),
                new G6GoalGoalRush(perceptionAndMemory)
        );
    }

    @Override
    public double priority(Goal goal, Strategy strategy) {
        if (strategy.equals(Strategy.OFFENSE)) {
            if (goal instanceof G6GoalExplore) return 0.0;
            if (goal instanceof G6GoalChangeRole) return 1.0;
            if (goal instanceof G6GoalRetrieveBlock) return 2.0;
            if (goal instanceof G6GoalGoalRush) return 3.0;
        }
        return -1;
    }

    @Override
    public double getMaxOffensivePercentage() {
        return 1.0;
    }
}
