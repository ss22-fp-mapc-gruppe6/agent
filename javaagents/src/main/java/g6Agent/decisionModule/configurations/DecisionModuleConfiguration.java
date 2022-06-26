package g6Agent.decisionModule.configurations;

import g6Agent.decisionModule.entities.Strategy;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

import java.util.List;

public interface DecisionModuleConfiguration {

    /**
     * Generates the Goals in this configuration
     * @return the List of possible Goals
     */

    List<Goal> generateGoals(PerceptionAndMemory perceptionAndMemory, Strategy strategy);
    /**
     * Gives the priority of the Goals in this configuration
     * @param goal the goal
     * @return the priority
     */
    double priority(Goal goal, Strategy strategy);

    /**
     *
     * @return the percentage of Agents who's strategy will be will be offensive
     */
    double getOffensivePercentage();
}
