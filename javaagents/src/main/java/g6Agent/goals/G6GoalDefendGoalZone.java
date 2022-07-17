package g6Agent.goals;

import g6Agent.actions.Clear;
import g6Agent.actions.G6Action;
import g6Agent.actions.Skip;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.decisionModule.manhattanDistanceMove.ManhattanDistanceMove;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class G6GoalDefendGoalZone implements Goal {
    private final PerceptionAndMemory perceptionAndMemory;

    public G6GoalDefendGoalZone(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }


    @Override
    public G6Action getNextAction() {
        //If sees EnemyAgent next to a Block clear action against Enemy Agent
        Point target =determineIfSeesEnemyAgentWithBlockAttached();
        if(target != null){
            return moveToTargetAndTackle(target);
        }

        //check if in GoalZone, if yes -> skip, else move to GoalZone
        return moveToClosestGoalZone();
    }

    @NotNull
    private G6Action moveToClosestGoalZone() {
        boolean isInGoalZone = perceptionAndMemory.getGoalZones().stream().anyMatch(point-> point.equals(new Point(0,0)));
        boolean doesntKnowAGoalZone = perceptionAndMemory.getGoalZones().isEmpty();
        if (isInGoalZone || doesntKnowAGoalZone){
            return new Skip();
        }
        Point closestGoalZone = perceptionAndMemory.getGoalZones()
                .stream()
                .min(Comparator.comparingInt(goalZone -> goalZone.manhattanDistanceTo(new Point(0,0))))
                .orElse(null);
        if (closestGoalZone == null) return new Skip();
        return moveTo(closestGoalZone);
    }

    @NotNull
    private G6Action moveToTargetAndTackle(Point target) {
        if (target.manhattanDistanceTo(new Point(0,0)) <= perceptionAndMemory.getCurrentRole().getClearActionMaximumDistance()){
            G6Action tackleAgent  = new Clear(target);
            if (tackleAgent.predictSuccess(perceptionAndMemory)) return tackleAgent;
        }
        Point closestPointNextToTarget = closestUnblockedPointNextTo(target);

        return moveTo(closestPointNextToTarget);
    }

    private Point closestUnblockedPointNextTo(Point target) {
       return Arrays
                .stream(Direction.allDirections())
                .map(direction -> direction.getNextCoordinate().add(target))
                .filter(point-> perceptionAndMemory.getBlocks()
                        .stream()
                        .noneMatch(block -> block.getCoordinates().equals(point)))
                .min(Comparator.comparingInt(point -> point.manhattanDistanceTo(new Point(0,0))))
                .orElse(target);
    }

    @NotNull
    private G6Action moveTo(Point closestPointNextToTarget) {
        G6Action moveToTarget = AStar.astarNextStep(closestPointNextToTarget, perceptionAndMemory)
                .orElse(ManhattanDistanceMove.nextAction(closestPointNextToTarget, perceptionAndMemory));
        if (moveToTarget.predictSuccess(perceptionAndMemory)) return moveToTarget;
        moveToTarget = AStar.astarNextStepWithAgents(closestPointNextToTarget, perceptionAndMemory)
                .orElse(ManhattanDistanceMove.nextAction(closestPointNextToTarget, perceptionAndMemory));
        return moveToTarget;
    }

    private Point determineIfSeesEnemyAgentWithBlockAttached() {
        if (perceptionAndMemory.getCurrentRole() == null) return null;
        List<Point> enemyAgentsNextToABlock = perceptionAndMemory.getEnemyAgents()
                .stream()
                .filter(agent -> perceptionAndMemory.getBlocks()    //has blocks next to ist
                        .stream()
                        .anyMatch(block -> block.getCoordinates().isAdjacentTo(agent)))
                .filter(agent-> agent.manhattanDistanceTo(new Point(0,0)) <= perceptionAndMemory.getCurrentRole().getVisionRange()) //is in vision range
                .toList();

        if (enemyAgentsNextToABlock.isEmpty()) return null;

        return enemyAgentsNextToABlock.stream().min(Comparator.comparingInt(x-> x.manhattanDistanceTo(new Point(0,0)))).orElse(null);
    }

    @Override
    public boolean isSucceding() {
        return preconditionsMet();
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalDefendGoalZone";
    }

    @Override
    public boolean preconditionsMet() {
        if (perceptionAndMemory.getGoalZones().isEmpty()) return false;
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        return perceptionAndMemory.getCurrentRole().canPerformAction("clear");
    }

}
