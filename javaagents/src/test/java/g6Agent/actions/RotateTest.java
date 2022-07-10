package g6Agent.actions;

import g6Agent.services.Point;
import g6Agent.services.Rotation;
import org.junit.Test;

import java.util.List;

import static g6Agent.services.Direction.EAST;
import static g6Agent.services.Direction.SOUTH;
import static org.junit.Assert.*;

public class RotateTest {

    @Test
    public void predictSuccess() {
        Rotate rotate = new Rotate(Rotation.CLOCKWISE);
        List<Point> attachments = List.of(new Point(1, 0));
        List<Point> obstacles = List.of(new Point(3,3), new Point(0,1),new Point(-1, 0));
        /*
        attachment at 1,0 should collide with 1,0 after being turned clockwise
         */
        Rotate.AttachmentCollidingWithObstacleException e = assertThrows(
                Rotate.AttachmentCollidingWithObstacleException.class,
                () -> rotate.predictSuccess(attachments, obstacles));
        assertEquals(new Point(1, 0), e.attachment);
        assertEquals(new Point(0, 1), e.collision);
        assertEquals(Rotation.CLOCKWISE, e.rotation);
    }
}