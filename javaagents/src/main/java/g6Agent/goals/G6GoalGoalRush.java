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
        for (Task task : perceptionAndMemory.getTasks()) {
            for (Block b : task.getRequirements()) {
                for (Block blockAttached : perceptionAndMemory.getAttachedBlocksToSelf()) {
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
        } //TODO Tasks with more than one block
    }


    @Override
    public G6Action getNextAction() {
        if (perceptionAndMemory.getGoalZones().isEmpty()) {
            return findGoalZone();
        } else {
            //If no task Selected, select another one, or skip
            if (task == null) chooseTask();
            if (task == null) return moveToGoalZone();
            boolean isTaskStillActive = false;
            for (Task t : perceptionAndMemory.getTasks()) {
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
                    //TODO Work together with other Agents to fullfill Task
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
        for (Block attached : perceptionAndMemory.getAttachedBlocksToSelf()) {
            if (requirement.getBlocktype().equals(attached.getBlocktype()) &&
                    requirement.getCoordinates().equals(attached.getCoordinates())) {
                return new Submit(task);
            } else {
                for (Block blockAttached : perceptionAndMemory.getAttachedBlocksToSelf()) {
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
        for (Direction d : Direction.allDirections()) {
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
        for (Block attachedBlock : perceptionAndMemory.getAttachedBlocksToSelf()) {
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
        if (fibbbonacciwalkCounter == fibonnaciWalkCurrent) {
            int temp = fibonnaciWalkCurrent;
            fibonnaciWalkCurrent = fibonnaciWalkCurrent + fibbonacciWalkFormer;
            fibbonacciWalkFormer = temp;
            fibonacciWalkDirection = fibonacciWalkDirection.rotate(Rotation.CLOCKWISE);
            fibbbonacciwalkCounter = 0;
        }
        G6Action action = moveTo(fibonacciWalkDirection);
        if (action instanceof Move) {
            fibbbonacciwalkCounter++;
        }
        return action;
    }

    @Override
    public boolean isSucceding() {
        //Has no Blocks Attached
        if (perceptionAndMemory.getAttachedBlocksToSelf().isEmpty()) {
            return false;
        }
        //Has no chosen Task
        if (task == null) {
            return false;
        }
        //Chosen Task is still active
        boolean taskStillActive = false;
        for (Task t : perceptionAndMemory.getTasks()) {
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
            for (Block blockAttached : perceptionAndMemory.getAttachedBlocksToSelf()) {
                if (blockAttached.getBlocktype().equals(b.getBlocktype())) {
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
        return lastAction.getName().equals("submit") && lastAction.getSuccessMessage().equals("success");
    }

    @Override
    public String getName() {
        return "G6GoalGoalRush";
    }


    private List<String> possibleRoleNames() {
        List<String> roleNames = new ArrayList<>();
        for (Role role : perceptionAndMemory.getPossibleRoles()) {
            for (String action : role.getPossibleActions()) {
                if (action.equals("submit")) {
                    roleNames.add(role.getName());
                }
            }
        }
        return roleNames;
    }
}
