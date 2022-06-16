package g6Agent.decisionModule;

import g6Agent.communicationModule.CommunicationModule;
import g6Agent.goals.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Module to generate and decide about the current Goal of an Agent
 */

public class DecisionModuleImplementation implements DecisionModule{
    private final PerceptionAndMemory perceptionAndMemory;
    private final CommunicationModule communicationModule;
    private Goal currentGoal;

    public DecisionModuleImplementation(PerceptionAndMemory perceptionAndMemory, CommunicationModule communicationModule) {
        this.perceptionAndMemory = perceptionAndMemory;
        this.communicationModule = communicationModule;
        this.currentGoal = new G6GoalExplore(perceptionAndMemory);
    }


    @Override
    public Goal revalidateGoal() {
        List<Goal> goal_options = generateGoals();
        goal_options = goal_options
                .stream()
                .filter(Goal::preconditionsMet)
                .collect(Collectors.toList());
        goal_options = filterWithInformationAboutFriendlyAgents(goal_options);
        Goal goal = selectGoalWithHighestPriority(goal_options);
        if (goal != null){
            if (priority(goal) > priority(currentGoal) || currentGoal.isFullfilled() || !currentGoal.isSucceding()) {
                currentGoal = goal;
            }
        }
        return currentGoal;
    }

    private Goal selectGoalWithHighestPriority(List<Goal> goal_options) {
    Goal goal = new G6GoalExplore(perceptionAndMemory);
    for (var g : goal_options){
        if (priority(g) > priority(goal)){
            goal = g;
        }
    }
    return goal;
    }

    private int priority(Goal goal) {
        if (goal instanceof G6GoalExplore) return 0;
        if (goal instanceof G6GoalChangeRole) return 1;
        if (goal instanceof G6GoalRetrieveBlock) return 2;
        if (goal instanceof G6GoalGoalRush) return 3;
        return -1;
    }

    private List<Goal> filterWithInformationAboutFriendlyAgents(List<Goal> goal_options) {
        //TODO filter with information from communicationModule
        return goal_options;
    }

    private List<Goal> generateGoals() {
    return List.of(
            new G6GoalExplore(perceptionAndMemory),
            new G6GoalChangeRole("worker", perceptionAndMemory),
            new G6GoalRetrieveBlock(perceptionAndMemory),
            new G6GoalGoalRush(perceptionAndMemory)
    );
    }
}
