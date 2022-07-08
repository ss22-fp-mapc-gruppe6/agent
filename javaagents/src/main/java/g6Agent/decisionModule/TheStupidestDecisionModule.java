package g6Agent.decisionModule;

import g6Agent.goals.*;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class TheStupidestDecisionModule implements DecisionModule {

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
        if (perceptionAndMemory.getLastAction() != null) {
            if (perceptionAndMemory.getLastAction().getName().equals("rotate")
                    && !perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")
                    && perceptionAndMemory.getEnergy() > 30) {
                return new G6GoalDig(perceptionAndMemory);
            }
        }
        if (perceptionAndMemory.getCurrentRole() != null) {
            //if rolezone in sight -> become a worker
            if ((!perceptionAndMemory.getCurrentRole().getName().equals("worker")) && perceptionAndMemory.getRoleZones().size() > 0) {
                currentGoal = (currentGoal.getName().equals("G6GoalChangeRole") && !currentGoal.isFullfilled() ? currentGoal : new G6GoalChangeRole("worker", perceptionAndMemory));
                return currentGoal;
            }
            boolean canDoAttach = false;
            boolean canDoSubmit = false;
            for (String actionName : perceptionAndMemory.getCurrentRole().getPossibleActions()) {
                if (actionName.equals("submit")) canDoSubmit = true;
                if (actionName.equals("attach")) canDoAttach = true;
            }
            if (canDoSubmit) {
                //if has blocks attached -> Go for Goal
                if (perceptionAndMemory.getDirectlyAttachedBlocks().size() > 0) {
                    boolean hasBlockMatchingTask = checkIfBlockMatchingTask();
                    if (hasBlockMatchingTask) {
                        currentGoal = (currentGoal.getName().equals("G6GoalGoalRush") && !currentGoal.isFullfilled()) ? currentGoal : new G6GoalGoalRush(perceptionAndMemory);
                        return currentGoal;
                    }
                }
            }
            if (canDoAttach) {
                //if dispenser is in sight -> retrieve block
                if (perceptionAndMemory.getDispensers().size() > 0) {
                    currentGoal = (currentGoal.getName().equals("G6GoalRetrieveBlock") && !currentGoal.isFullfilled()) ? currentGoal : new G6GoalRetrieveBlock(perceptionAndMemory);
                    return currentGoal;
                }
            }
        }

        /*
        //if more obstacles than threshold are in sight -> dig
        if (perceptionAndMemory.getCurrentRole() != null) {
            int counter = 0;
            for (Point obstacle : perceptionAndMemory.getObstacles()) {
                if (obstacle.manhattanDistanceTo(new Point(0,0)) <= perceptionAndMemory.getCurrentRole().getVisionRange()){
                    counter++;
                }
            }
            if (counter > OBSTACLE_THRESHOLD && perceptionAndMemory.getEnergy() > 50) {
                currentGoal = (currentGoal.getName().equals("G6GoalDig") && !currentGoal.isFullfilled()) ? currentGoal : new G6GoalDig(perceptionAndMemory);
                return currentGoal;
            }
        }


         */
        //if nothing else -> Explore
        currentGoal = (currentGoal.getName().equals("G6GoalExplore") && !currentGoal.isFullfilled()) ? currentGoal : new G6GoalExplore(perceptionAndMemory);
        return currentGoal;
    }

    private boolean checkIfBlockMatchingTask() {
        boolean hasBlockMatchingTask = false;
        for (Block attchedBlock : perceptionAndMemory.getDirectlyAttachedBlocks()) {
            for (Task t : perceptionAndMemory.getActiveTasks()) {
                for (Block requirement : t.getRequirements()) {
                    if (requirement.getBlocktype().equals(attchedBlock.getBlocktype())) {
                        hasBlockMatchingTask = true;
                        break;
                    }
                }
            }
        }


        return hasBlockMatchingTask;
    }
}
