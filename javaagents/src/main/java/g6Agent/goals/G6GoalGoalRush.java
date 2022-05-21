package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class G6GoalGoalRush implements Goal {
    private Task task;
    private Direction fibonacciWalkDirection = Direction.NORTH;
    private int fibonnaciWalkCurrent = 1;
    private int fibbonacciWalkFormer = 0;
    private int fibbbonacciwalkCounter = 0;
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalGoalRush(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
        //check if attached blocks are part of a task, choose the task with an attached block matching
        for (Task task : perceptionAndMemory.getTasks()) {
            for (Block b : task.getRequirements()) {
                for (Block blockAttached : perceptionAndMemory.getAttachedBlocks()) {
                    if (blockAttached.getBlocktype().equals(b.getBlocktype())) {
                        this.task = task;
                    }
                }
            }
        }
        for (Task task : perceptionAndMemory.getTasks()) {
            for (Block b : task.getRequirements()) {
                for (Block blockAttached : perceptionAndMemory.getAttachedBlocks()) {
                    if (blockAttached.getBlocktype().equals(b.getBlocktype())) {
                        this.task = task;
                        break;
                    }
                }
            }
        }
    }


    @Override
    public G6Action getNextAction() {
        if(perceptionAndMemory.getGoalZones().isEmpty()){
            return findGoalZone();
        } else{
            boolean inGoalZone = checkIfInGoalZone();
            if(inGoalZone) {
                if (task.getRequirements().size() == 1){
                   return rotateAndSubmit();
                } else{
                    //TODO Work together with other Agents to fullfill Task
                    return new Skip();
                }
            } else {
                return moveToGoalZone();
            }
        }
    }

    private G6Action rotateAndSubmit() {
        Block requirement =  task.getRequirements().get(0);
        for (Block attached : perceptionAndMemory.getAttachedBlocks()){
            if(requirement.getBlocktype().equals(attached.getBlocktype()) && requirement.getCoordinates().equals(attached.getCoordinates())){
                return new Submit(task);
            }
            else{
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        return null;
    }

    private boolean checkIfInGoalZone() {
        boolean inGoalZone = false;
        for(Point goalZone : perceptionAndMemory.getGoalZones()) {
            if(goalZone.equals(new Point(0,0))){
                inGoalZone = true;
            }
        }
        return inGoalZone;
    }

    private G6Action moveToGoalZone() {
        Point closestGoalZone = perceptionAndMemory.getRoleZones().get(0);
        for (Point goalZone : perceptionAndMemory.getRoleZones()){
            if(goalZone.manhattanDistanceTo(new Point(0,0)) < closestGoalZone.manhattanDistanceTo(new Point(0,0))){
                closestGoalZone = goalZone;
            }
        }

        Direction direction = Direction.WEST;
        for (Direction d : Direction.allDirections()){
            if (d.getNextCoordinate().manhattanDistanceTo(closestGoalZone) < direction.getNextCoordinate().manhattanDistanceTo(closestGoalZone)){
                direction = d;
            }
        }
        return moveTo(direction);
    }

    private G6Action moveTo(Direction direction) {
        for(Block attachedBlock : perceptionAndMemory.getAttachedBlocks()){
            if(!attachedBlock.getCoordinates().invert().equals(direction.getNextCoordinate())){
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        for(Point obstacle : perceptionAndMemory.getObstacles()){
            if(direction.getNextCoordinate().equals(obstacle)){
                return new Clear(obstacle);
            }
        }
        return new Move(direction);
    }

    private G6Action findGoalZone() {
        return fibbonacciWalk();
    }

    /**
     * Placeholder for something better, should cover a lot of ground
     * @return the Movement Direction
     */
    private G6Action fibbonacciWalk() {
        if (fibbbonacciwalkCounter == fibonnaciWalkCurrent){
            int temp = fibonnaciWalkCurrent;
            fibonnaciWalkCurrent = fibonnaciWalkCurrent + fibbonacciWalkFormer;
            fibbonacciWalkFormer = temp;
            fibonacciWalkDirection = fibonacciWalkDirection.rotate(Rotation.CLOCKWISE);
            fibbbonacciwalkCounter = 0;
        }
        G6Action action = moveTo(fibonacciWalkDirection);
        if(action instanceof Move){
            fibbbonacciwalkCounter++;
        }
        return action;
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
                break;
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
        LastActionMemory lastAction = perceptionAndMemory.getLastAction();
        if (lastAction == null) return false;
        if (lastAction.getName().equals("submit") && lastAction.getSuccessMessage().equals("success")) return true;
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalGoalRush";
    }


    private List<String> possibleRoleNames(){
        List<String> roleNames = new ArrayList<>();
        for(Role role : perceptionAndMemory.getPossibleRoles()){
            boolean submitFound = false;
            for (String action : role.getPossibleActions()){
                if (action.equals("submit")){
                    roleNames.add(role.getName());
                }
            }
        }
        return roleNames;
    }
}
