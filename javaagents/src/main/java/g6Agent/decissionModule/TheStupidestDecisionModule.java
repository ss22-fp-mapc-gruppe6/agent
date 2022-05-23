package g6Agent.decissionModule;

import g6Agent.goals.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class TheStupidestDecisionModule implements DecisionModule{

    int OBSTACLE_THRESHOLD = 22;
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

        //if blocked clear your way out
        if(perceptionAndMemory.getLastAction() != null){
            if(perceptionAndMemory.getLastAction().getName().equals("rotate")
                    && !perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")
                    && perceptionAndMemory.getEnergy() > 30){
                return new G6GoalDig(perceptionAndMemory);
            }
        }

        //if rolezone in sight -> become a worker
            if ((!perceptionAndMemory.getCurrentRole().getName().equals("worker")) && perceptionAndMemory.getRoleZones().size() > 0) {
                currentGoal = (currentGoal.getName().equals("G6GoalChangeRole") && !currentGoal.isFullfilled() ? currentGoal : new G6GoalChangeRole("worker", perceptionAndMemory));
                return currentGoal;
            }

            if(perceptionAndMemory.getCurrentRole().getName().equals("worker")) {
                //if has blocks attached -> Go for Goal
                if (perceptionAndMemory.getAttached().size() > 0) {
                    currentGoal = (currentGoal.getName().equals("G6GoalGoalRush") && !currentGoal.isFullfilled()) ? currentGoal : new G6GoalGoalRush(perceptionAndMemory);
                        return currentGoal;

                }


                //if dispenser is in sight -> retrieve block
                if (perceptionAndMemory.getDispensers().size() > 0) {
                    currentGoal = (currentGoal.getName().equals("G6GoalRetrieveBlock") && !currentGoal.isFullfilled()) ? currentGoal : new G6GoalRetrieveBlock(perceptionAndMemory);
                    return currentGoal;
                }
            }


        //if more obstacles than threshold are in sight -> dig
        if (perceptionAndMemory.getObstacles().size() > OBSTACLE_THRESHOLD && perceptionAndMemory.getEnergy() > 50){
            currentGoal =  (currentGoal.getName().equals("G6GoalDig")&& !currentGoal.isFullfilled())? currentGoal : new G6GoalDig(perceptionAndMemory);
            return currentGoal;
        }

        //if nothing else -> Explore
        currentGoal = (currentGoal.getName().equals("G6GoalExplore")&& !currentGoal.isFullfilled())? currentGoal : new G6GoalExplore(perceptionAndMemory);
        return currentGoal;
    }
}
