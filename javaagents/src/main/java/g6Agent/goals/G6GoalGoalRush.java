package g6Agent.goals;

import g6Agent.actions.G6Action;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

public class G6GoalGoalRush implements Goal {
    Task task;
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalGoalRush(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
        //check if attached blocks are part of a task, choose the task with an attached block matching
    }


    @Override
    public G6Action getNextAction() {

        //SubGoal : find Goal Zone

        //if all blocks attached match task, rotate till matches pattern, -> submit

        //Subgoal : build Task together with other agents


        return null;
    }

    @Override
    public boolean isSucceding() {
        //Has no Blocks Attached
        if(perceptionAndMemory.getAttachedBlocks().isEmpty()){
            return false;
        }
        //Has no chosen Task
        if(task == null){
            return false;
        }
        //Chosen Task is still active
        boolean taskStillActive = false;
        for(Task t : perceptionAndMemory.getTasks()){
            if (t.getName().equals(task.getName())){
                taskStillActive=true;
            }
        }
        if(!taskStillActive){
            this.task = null;
            return false;
        }
        //Has Blocks matching Task
        for(Block b : task.getRequirements()){
            for (Block blockAttached : perceptionAndMemory.getAttachedBlocks()){
                if (blockAttached.getBlocktype().equals(b.getBlocktype())){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalGoalRush";
    }
}
