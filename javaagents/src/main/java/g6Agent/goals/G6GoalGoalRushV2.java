package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class G6GoalGoalRushV2 implements Goal {
    private Task task;
    private Direction fibonacciWalkDirection = Direction.NORTH;
    private int fibonnaciWalkCurrent = 1;
    private int fibbonacciWalkFormer = 1;
    private int fibbbonacciwalkCounter = 0;
    private final PerceptionAndMemory perceptionAndMemory;


    public G6GoalGoalRushV2(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
        //check if attached blocks are part of a task, choose the tasks with an attached block matching
        chooseTask();
    }

    @Override
    public G6Action getNextAction() {
        if (perceptionAndMemory.getGoalZones().isEmpty()) {
            return exploreMap();
        } else {
            if (task == null) chooseTask();

            if (task == null) return moveToGoalZone(); //currently no task with the attached Block
            boolean isTaskStillActive = checkIfTaskStillValid();
            if (!isTaskStillActive) {
                chooseTask();
            }
            boolean inGoalZone = checkIfInGoalZone();
            if (inGoalZone) {
                if (task == null) return new Skip();
                if (task.getRequirements().size() == 1) { //sanity check
                    return rotateAndSubmit(task.getRequirements().get(0));
                } else {
                    return new Skip();
                }
            } else {
                return moveToGoalZone();
            }
        }
    }

    @NotNull
    private G6Action rotateAndSubmit(Block block) {
        //rotate block to match task
        Block attachedBlock = perceptionAndMemory.getDirectlyAttachedBlocks()
                .stream()
                .filter(x-> x.getBlocktype().equals(block.getBlocktype()))
                .findFirst()
                .orElseThrow();

        //rotation clockwise
        if (attachedBlock.getCoordinates().rotate(Rotation.CLOCKWISE).equals(block.getCoordinates())){
            G6Action rotation = new Rotate(Rotation.CLOCKWISE);
            return rotateOrClear(block, rotation);
        }
        // rotation counterclockwise
        else if (attachedBlock.getCoordinates().rotate(Rotation.COUNTERCLOCKWISE).equals(block.getCoordinates())){
            G6Action rotation = new Rotate(Rotation.COUNTERCLOCKWISE);
            return rotateOrClear(block, rotation);
        }
        //is opposite
        else {
            G6Action rotationClockwise = new Rotate(Rotation.CLOCKWISE);
            G6Action rotationCounterclockwise = new Rotate(Rotation.COUNTERCLOCKWISE);
            if (rotationClockwise.predictSuccess(perceptionAndMemory)) return rotationClockwise;
            if(rotationCounterclockwise.predictSuccess(perceptionAndMemory)) return rotationCounterclockwise;
            return new Clear(block.getCoordinates().rotate(Rotation.COUNTERCLOCKWISE));
        }
    }

    @NotNull
    private G6Action rotateOrClear(Block block, G6Action rotation) {
        if (rotation.predictSuccess(perceptionAndMemory)){
            return rotation;
        } else {
            return new Clear(block.getCoordinates());
        }
    }
    private boolean checkIfInGoalZone() {
        return perceptionAndMemory.getGoalZones().stream().anyMatch(x -> x.equals(new Point(0,0)));
    }

    private G6Action moveToGoalZone() {
        if (perceptionAndMemory.getGoalZones().isEmpty()) return exploreMap();
        Point closestGoalZone = perceptionAndMemory.getGoalZones().stream().min(Comparator.comparingInt(a-> a.manhattanDistanceTo(new Point(0,0)))).orElseThrow() ;
        return AStar.astarNextStep(closestGoalZone, perceptionAndMemory).orElseThrow();
    }

    private G6Action exploreMap() {
        return fibbonacciWalk();
    }

    private boolean checkIfTaskStillValid() {
        boolean isTaskStillActive = false;
        for (Task t : perceptionAndMemory.getActiveTasks()) {
            if (t.getName().equals(task.getName())) {
                isTaskStillActive = true;
                break;
            }
        }
        return isTaskStillActive;
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
        boolean taskStillActive = checkIfTaskStillValid();
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

    @Override
    public boolean isFullfilled() {
        LastActionMemory lastAction = perceptionAndMemory.getLastAction();
        if (lastAction == null) return false;
        return lastAction.getName().equals("submit") && lastAction.getSuccessMessage().equals("success");
    }

    @Override
    public String getName() {
        return "G6GoalGoalRushV2";
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
}
