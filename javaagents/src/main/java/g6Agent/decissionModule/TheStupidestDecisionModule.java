package g6Agent.decissionModule;

import g6Agent.goals.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class TheStupidestDecisionModule implements DecisionModule{

    int OBSTACLE_THRESHOLD = 5;
    Goal currentGoal;
    PerceptionAndMemory perceptionAndMemory;

    public TheStupidestDecisionModule(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
        this.currentGoal = new G6GoalExplore(perceptionAndMemory);
    }


    @Override
    public Goal revalidateGoal() {
        //if succeding and is an important goal stick to it!
        if ((currentGoal.isSucceding() && !currentGoal.isFullfilled())) {
            if (!currentGoal.getName().equals("G6GoalExplore") && !currentGoal.getName().equals("G6GoalDig")) {
                return currentGoal;
            }
        }
        //walk decision tree

        //if has blocks attached -> Go for Goal
        if (perceptionAndMemory.getAttached().size() > 0){
               return (currentGoal.getName().equals("G6GoalGoalRush") && !currentGoal.isFullfilled())? currentGoal : new G6GoalGoalRush(perceptionAndMemory);
        }

        //if dispenser is in sight -> retrieve block
        if (perceptionAndMemory.getDispensers().size() > 0){
            return (currentGoal.getName().equals("G6GoalRetrieveBlock")&& !currentGoal.isFullfilled())? currentGoal : new G6GoalRetrieveBlock(perceptionAndMemory);
        }

        //if more obstacles than threshold are in sight -> dig
        if (perceptionAndMemory.getObstacles().size() > OBSTACLE_THRESHOLD){
            return (currentGoal.getName().equals("G6GoalDig")&& !currentGoal.isFullfilled())? currentGoal : new G6GoalDig(perceptionAndMemory);
        }

        //if nothing else -> Explore
        return (currentGoal.getName().equals("G6GoalExplore")&& !currentGoal.isFullfilled())? currentGoal : new G6GoalExplore(perceptionAndMemory);
    }
}
