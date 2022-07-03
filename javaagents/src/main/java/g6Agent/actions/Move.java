package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent moves in the specified direction (north, west, est south)
 */

public class Move extends Action implements G6Action {

    private final List<Direction> directions;

    /**
     * Moves the agent in the specified directions. If the agent is currently allowed to move more than one cell, multiple directions can be given.
     *
     * @param directions the directions to move
     */

    public Move(Direction... directions) {
        super("move", Arrays.stream(directions).map(Direction::getIdentifier).collect(Collectors.toList()));
        this.directions = Arrays.stream(directions).collect(Collectors.toUnmodifiableList());
    }

    public boolean predictSuccess(final List<Point> attachments, final List<Point> obstacles) throws Exception {
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
    public static class AttachmentCollidingWithObstacleException extends Exception {
        Point attachment;
        List<Direction> directionsTaken;
        Point collision;
    }
}
