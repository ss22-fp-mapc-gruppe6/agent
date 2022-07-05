package g6Agent.actions;

import g6Agent.services.Point;
import org.junit.Test;

import java.util.List;

import static g6Agent.services.Direction.*;
import static org.junit.Assert.*;

public class MoveTest {

    @Test
    public void predictSuccess() {
        Move move = new Move(SOUTH, EAST, SOUTH, EAST);
        List<Point> attachments = List.of(new Point(1, 0));
        List<Point> obstacles = List.of(new Point(3,3), new Point(2,3),new Point(2, 2));
        /*
        attachment at 1,0 should collide with 2,2 after going south twice and east once
         */
        Move.AttachmentCollidingWithObstacleException e = assertThrows(
                Move.AttachmentCollidingWithObstacleException.class,
                () -> move.predictSuccess(attachments, obstacles));
        assertEquals(new Point(1, 0), e.attachment);
        assertEquals(new Point(2, 2), e.collision);
        assertEquals(3, e.directionsTaken.size());
    }
}