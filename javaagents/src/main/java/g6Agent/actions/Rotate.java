package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Rotate extends Action implements G6Action {

    public final Rotation rotation;
    /**
     * Rotates the agent (and all attached things) 90 degrees in the given direction. For each attached thing, its final position after the rotation has to be free.
     *
     * @param rotation the rotation direction (clockwise or counterclockwise).
     */
    public Rotate(Rotation rotation) {
        super("rotate", rotation.getIdentifier());
        this.rotation = rotation;
    }


    public boolean predictSuccess(final Collection<Point> attachments, final Collection<Point> obstacles) throws AttachmentCollidingWithObstacleException {
        for (Point point : attachments) {
            Point rotate = point.rotate(rotation);
            if (obstacles.contains(rotate)) {
                throw new AttachmentCollidingWithObstacleException(point, rotation, rotate);
            }
        }
        return true;
    }

    @AllArgsConstructor
    @Getter
    public static class AttachmentCollidingWithObstacleException extends Exception {
        Point attachment;
        Rotation rotation;
        Point collision;
    }

}
