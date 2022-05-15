package g6Agent.decissionModule;

import g6Agent.goals.Goal;

/**
 * Interface for a Decision Module, which decides which Goals an Agent pursues with his given belief.
 */

public interface DecisionModule {
    Goal revalidateGoal();
}
