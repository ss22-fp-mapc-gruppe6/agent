package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Agent moves in the specified direction (north, west, est south)
 */

public class Move extends Action implements G6Action {

    public final List<Direction> directions;

    /**
     * Moves the agent in the specified directions. If the agent is currently allowed to move more than one cell, multiple directions can be given.
     *
     * @param directions the directions to move
     */

    public Move(Direction... directions) {
        super("move", Arrays.stream(directions).map(Direction::getIdentifier).collect(Collectors.toList()));
        this.directions = Arrays.stream(directions).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) {
        try {
            if (directions.isEmpty()) return false;
            List<Point> otherAgents = Stream.concat(perceptionAndMemory.getFriendlyAgents().stream(), perceptionAndMemory.getEnemyAgents().stream()).toList();
            boolean isUnblockedByAgent = otherAgents.stream().noneMatch(point -> point.equals(directions.get(0).getNextCoordinate()));
            return predictSuccess(
                    perceptionAndMemory.getDirectlyAttachedBlocks().stream().map(x -> x.getCoordinates()).toList(),
                    perceptionAndMemory.getObstacles())
                    && isUnblockedByAgent;
        }catch (Exception e){
            return false;
        }
    }

    public boolean predictSuccess(final List<Point> attachments, final Collection<Point> obstacles) throws AttachmentCollidingWithObstacleException {
        for (int a = 0; a < attachments.size(); a++) {
            Point attachmentMoved = attachments.get(a);
            for (int d = 0; d < directions.size(); d++) {
                Direction direction = directions.get(d);
                attachmentMoved = attachmentMoved.add(direction.getNextCoordinate());
                for (Point obstacle : obstacles) {
                    if (attachmentMoved.equals(obstacle)) {
                        throw new AttachmentCollidingWithObstacleException(
                                attachments.get(a),
                                directions.stream().limit(d + 1).toList(),
                                obstacle
                        );
                    }
                }
            }
        }
        return true;
    }

    @AllArgsConstructor
    @Getter
    public static class AttachmentCollidingWithObstacleException extends Exception {
        Point attachment;
        List<Direction> directionsTaken;
        Point collision;
    }
}
