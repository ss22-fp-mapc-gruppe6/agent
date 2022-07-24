package g6Agent.goals;

import g6Agent.actions.Clear;
import g6Agent.actions.G6Action;
import g6Agent.actions.Move;
import g6Agent.actions.Rotate;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;

import java.util.Arrays;
import java.util.List;

public class G6GoalExploreV2 implements Goal{
    private final PerceptionAndMemory perceptionAndMemory;
    private Direction fibonacciWalkDirection = Direction.random();
    private int fibonnaciWalkCurrent = 2;
    private int fibbonacciWalkFormer = 1;
    private int fibbbonacciwalkCounter = 0;

    public G6GoalExploreV2(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        if (perceptionAndMemory.getLastAction().getName().equals("rotate") && !perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")) {
            List<Point> adjacentObstacles = perceptionAndMemory.getObstacles().stream().filter(Point::isAdjacent).toList();
            if (!adjacentObstacles.isEmpty()) {
                return new Clear(adjacentObstacles.get(0));
            }
            List<Move> possibleMoves = Arrays.stream(Direction.allDirections()).map(direction -> new Move(direction)).filter(move -> move.predictSuccess(perceptionAndMemory)).toList();
            if (!possibleMoves.isEmpty()) {
                return possibleMoves.stream().findFirst().orElseThrow();
            }
        }

        return fibbonacciWalk();
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
        G6Action action = AStar.astarNextStepWithAgents(fibonacciWalkDirection.getNextCoordinate().multiply(fibbbonacciwalkCounter - fibonnaciWalkCurrent), perceptionAndMemory)
                .orElse(moveTo(fibonacciWalkDirection));
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
                    if (obstacle.equals(direction.rotate(Rotation.CLOCKWISE).getNextCoordinate()) || obstacle.equals(direction.getNextCoordinate().invert())) {
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

    @Override
    public boolean isSucceding() {
        return true;
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalExploreV2";
    }

    @Override
    public boolean preconditionsMet() {
        return true;
    }

}
