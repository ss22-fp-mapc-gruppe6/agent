package g6Agent.decisionModule.configurations;

import g6Agent.decisionModule.entities.Strategy;
import g6Agent.goals.*;
import g6Agent.goals.old.G6GoalRetrieveBlock;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

import java.util.ArrayList;
import java.util.List;

public class NewTestConfig implements DecisionModuleConfiguration{
    @Override
    public List<Goal> generateGoals(PerceptionAndMemory perceptionAndMemory, Strategy strategy) {

        List<Goal> goals = new ArrayList<>();
        goals.add(new G6GoalExplore(perceptionAndMemory));
        goals.add(new G6GoalChangeRole(strategy.getPreferredRoleName(), perceptionAndMemory));
        goals.add(new G6GoalRetrieveBlock(perceptionAndMemory));
        for (Task task : perceptionAndMemory.getActiveTasks()){
            goals.add(new G6GoalFulfillSingleTaskV1(perceptionAndMemory, task.getName()));
        }

        return goals;
    }

    @Override
    public double priority(Goal goal, Strategy strategy) {
        if (strategy.equals(Strategy.OFFENSE)) {
            if (goal instanceof G6GoalExplore) return 0.0;
            if (goal instanceof G6GoalChangeRole) return 1.0;
            if (goal instanceof G6GoalFulfillSingleTaskV1) return 2.0;
        }
        return -1;
    }

    @Override
    public double getMaxOffensivePercentage() {
        return 1.0;
    }
}
