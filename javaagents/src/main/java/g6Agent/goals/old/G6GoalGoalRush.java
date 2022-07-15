package g6Agent.goals.old;

import g6Agent.actions.*;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class G6GoalGoalRush implements Goal {
    private Task task;
    private Direction fibonacciWalkDirection = Direction.NORTH;
    private int fibonnaciWalkCurrent = 1;
    private int fibbonacciWalkFormer = 1;
    private int fibbbonacciwalkCounter = 0;
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalGoalRush(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
        //check if attached blocks are part of a task, choose the tasks with an attached block matching
        chooseTask();
    }

    private void chooseTask() {
        List<Task> possibleTasks = new ArrayList<>();
        for (Task task : perceptionAndMemory.getActiveTasks()) {
            for (Block b : task.getRequirements()) {
                for (Block blockAttached : perceptionAndMemory.getDirectlyAttachedBlocks()) {
                    if (blockAttached.getBlocktype().equals(b.getBlocktype())) {
                        possibleTasks.add(task);
                        break;
                    }
                }
            }
        }
        //accept a task with only one block needed
        for (Task t : possibleTasks) {
            if (t.getRequirements().size() == 1) {
                this.task = t;
            }
        }
    }


    @Override
    public G6Action getNextAction() {
        Clear blockingobstacle = IfRotateFailedBreakFree();
        if (blockingobstacle != null) return blockingobstacle;
        if (perceptionAndMemory.getGoalZones().isEmpty()) {
            return findGoalZone();
        } else {
            //If no task Selected, select another one, or skip
            //TODO neu machen!!!!
            if (task == null) chooseTask();
            if (task == null) return moveToGoalZone();
            boolean isTaskStillActive = false;
            for (Task t : perceptionAndMemory.getActiveTasks()) {
                if (t.getName().equals(task.getName())) {
                    isTaskStillActive = true;
                    break;
                }
            }
            if (!isTaskStillActive) {
                chooseTask();
            }

            boolean inGoalZone = checkIfInGoalZone();
            if (inGoalZone) {
                if (task == null) return new Skip();
                if (task.getRequirements().size() == 1) {
                    return rotateAndSubmit();
                } else {
                    return new Skip();
                }
            } else {
                return moveToGoalZone();
            }
        }
    }

    private G6Action rotateAndSubmit() {
        Block requirement = task.getRequirements().get(0);

        //move one step in
        //TODO Needs better logic
        for (Direction direction : Direction.allDirections()) {
            G6Action actionToMoveIn = moveToCenterOfGoalZone(direction);
            if (actionToMoveIn != null) return actionToMoveIn;
        }
        for (Block attached : perceptionAndMemory.getDirectlyAttachedBlocks()) {
            if (requirement.getBlocktype().equals(attached.getBlocktype()) &&
                    requirement.getCoordinates().equals(attached.getCoordinates())) {
                return new Submit(task);
            } else {
                for (Block blockAttached : perceptionAndMemory.getDirectlyAttachedBlocks()) {
                    for (Point obstacle : perceptionAndMemory.getObstacles()) {
                        if (obstacle.equals(blockAttached.getCoordinates().rotate(Rotation.CLOCKWISE))
                                || obstacle.equals(blockAttached.getCoordinates().rotate(Rotation.COUNTERCLOCKWISE))) {
                            return new Clear(obstacle);
                        }
                    }
                }
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        return null;
    }

    private G6Action moveToCenterOfGoalZone(Direction direction) {
        boolean isDirectionInGoalZone = false;
        boolean isOppositeDirectionInGoalZone = false;
        for (Point goalZone : perceptionAndMemory.getGoalZones()) {
            if (direction.getNextCoordinate().equals(goalZone)) isDirectionInGoalZone = true;
            if (direction.getNextCoordinate().invert().equals(goalZone)) isOppositeDirectionInGoalZone = true;
        }
        if (isDirectionInGoalZone && !isOppositeDirectionInGoalZone) {

            for (Point obstacle : perceptionAndMemory.getObstacles()) {
                if (obstacle.equals(direction.getNextCoordinate())) {
                    return new Clear(obstacle);
                }
            }
            return moveTo(direction);
        }
        return null;
    }

    private boolean checkIfInGoalZone() {
        for (Point goalZone : perceptionAndMemory.getGoalZones()) {
            if (goalZone.equals(new Point(0, 0))) {
                return true;
            }
        }
        return false;
    }

    private G6Action moveToGoalZone() {
        Point closestGoalZone = perceptionAndMemory.getGoalZones().get(0);
        for (Point goalZone : perceptionAndMemory.getGoalZones()) {
            if (goalZone.manhattanDistanceTo(new Point(0, 0)) < closestGoalZone.manhattanDistanceTo(new Point(0, 0))) {
                closestGoalZone = goalZone;
            }
        }

        Direction direction = Direction.WEST;

        List<Direction> directionsUnblockedByFriendlyAgents = new ArrayList<>(4);
        for (Direction d : Direction.allDirections()) {
            boolean isUnblocked = true;
            for (Point agentposition : perceptionAndMemory.getFriendlyAgents()) {
                if (agentposition.equals(d.getNextCoordinate())) {
                    isUnblocked = false;
                    break;
                }
            }
            if (isUnblocked) directionsUnblockedByFriendlyAgents.add(d);
        }
        for (Direction d : directionsUnblockedByFriendlyAgents) {
            if (d.getNextCoordinate().manhattanDistanceTo(closestGoalZone) < direction.getNextCoordinate().manhattanDistanceTo(closestGoalZone)) {
                boolean isBlocked = checkIfBlockedByFriendlyAgents(d);
                if (!isBlocked) {
                    direction = d;
                }
            }
        }
        return moveTo(direction);
    }

    private boolean checkIfBlockedByFriendlyAgents(Direction d) {
        boolean isBlocked = false;
        for (Point friendlyAgentsPosition : perceptionAndMemory.getFriendlyAgents()) {
            if (friendlyAgentsPosition.equals(d.getNextCoordinate())) {
                isBlocked = true;
                break;
            }
        }
        return isBlocked;
    }

    private G6Action moveTo(Direction direction) {
        for (Block attachedBlock : perceptionAndMemory.getDirectlyAttachedBlocks()) {
            if (!attachedBlock.getCoordinates().invert().equals(direction.getNextCoordinate())) {
                for (Point obstacle : perceptionAndMemory.getObstacles()) {
                    if (obstacle.equals(direction.rotate(Rotation.CLOCKWISE).getNextCoordinate())
                            || obstacle.equals(direction.getNextCoordinate().invert())
                            || obstacle.equals(direction.rotate(Rotation.COUNTERCLOCKWISE).getNextCoordinate())) {
                        return new Clear(obstacle);
                    }
                }
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        for (Point obstacle : perceptionAndMemory.getObstacles()) {
            if (direction.getNextCoordinate().equals(obstacle)) {
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
     *
     * @return the Movement Direction
     */
    private G6Action fibbonacciWalk() {
        if (perceptionAndMemory.getLastAction() != null
                && perceptionAndMemory.getLastAction().getName().equals("move")
                && !perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")) {
            skipToNextDirection();
        }
        if (fibbbonacciwalkCounter == fibonnaciWalkCurrent) {
            skipToNextDirection();
        }
        G6Action action = moveTo(fibonacciWalkDirection);
        if (action instanceof Move) {
            fibbbonacciwalkCounter++;
        }
        return action;
    }

    private void skipToNextDirection() {
        int temp = fibonnaciWalkCurrent;
        fibonnaciWalkCurrent = fibonnaciWalkCurrent + fibbonacciWalkFormer;
        fibbonacciWalkFormer = temp;
        fibonacciWalkDirection = fibonacciWalkDirection.rotate(Rotation.CLOCKWISE);
        fibbbonacciwalkCounter = 0;
    }

    @Override
    public boolean isSucceding() {
        //Has no Blocks Attached
        if (perceptionAndMemory.getDirectlyAttachedBlocks().isEmpty()) {
            return false;
        }
        //Has no chosen Task
        if (task == null) {
            return false;
        }
        //Chosen Task is still active
        boolean taskStillActive = false;
        for (Task t : perceptionAndMemory.getActiveTasks()) {
            if (t.getName().equals(task.getName())) {
                taskStillActive = true;
                break;
            }
        }
        if (!taskStillActive) {
            this.task = null;
            return false;
        }
        if (!perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")) {
            return false;
        }
        //Has Blocks matching Task
        for (Block b : task.getRequirements()) {
            for (Block blockAttached : perceptionAndMemory.getDirectlyAttachedBlocks()) {
                if (blockAttached.getBlocktype().equals(b.getBlocktype())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private Clear IfRotateFailedBreakFree() {
        if(perceptionAndMemory.getLastAction() != null){
            if(perceptionAndMemory.getLastAction().getName().equals("rotate")
                    && !perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")
                    && perceptionAndMemory.getEnergy() > 30){
                //find closest obstacle
                Point closestObstacle = perceptionAndMemory.getObstacles().get(0);
                for(Point obstacle : perceptionAndMemory.getObstacles()){

                    if (obstacle.manhattanDistanceTo(new Point(0,0)) < closestObstacle.manhattanDistanceTo(new Point(0,0))){
                        closestObstacle = obstacle;
                    }
                }
                //if in Range -> clear
                if(perceptionAndMemory.getCurrentRole() != null) {
                    if (closestObstacle.manhattanDistanceTo(new Point(0, 0)) <= perceptionAndMemory.getCurrentRole().getClearActionMaximumDistance()) {
                        return new Clear(closestObstacle);
                    }
                }
            }
        }
        return null;
    }


    @Override
    public boolean isFullfilled() {
        LastActionMemory lastAction = perceptionAndMemory.getLastAction();
        if (lastAction == null) return false;
        return lastAction.getName().equals("submit") && lastAction.getSuccessMessage().equals("success");
    }

    @Override
    public String getName() {
        return "G6GoalGoalRush";
    }

    @Override
    public boolean preconditionsMet() {
        Role currentRole = perceptionAndMemory.getCurrentRole();
        if (currentRole == null) return false;
        if (!currentRole.canPerformAction("attach")  || !currentRole.canPerformAction("submit")){
            return false;
        }
        if(perceptionAndMemory.getDirectlyAttachedBlocks().isEmpty()) return false;
        return checkIfBlockMatchingTask();
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
