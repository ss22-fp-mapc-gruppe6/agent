package g6Agent.decisionModule;

import g6Agent.actions.Clear;
import g6Agent.actions.Move;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class PointActionTest {
    @Test
    public void moveArg() {
        var o1 = new PointAction(new Point(0, 0), new Move(Direction.NORTH), new Point(0, 0));
        var o2 = new PointAction(new Point(0, 0), new Move(Direction.EAST), new Point(0, 0));
        assertNotEquals(o1, o2);
    }

    @Test
    public void moveVsClear() {
        var o1 = new PointAction(new Point(0, 0), new Move(Direction.NORTH), new Point(0, 0));
        var o2 = new PointAction(new Point(0, 0), new Clear(new Point(0, -1)), new Point(0, 0));
        assertNotEquals(o1, o2);
    }

    @Test
    public void compareTo() {
        var o1 = new PointAction(new Point(0, 0), new Move(Direction.NORTH), new Point(0, 0));
        var o2 = new PointAction(new Point(0, 0), new Clear(new Point(0, -1)), new Point(0, 0));
        assertNotEquals(0, o1.compareTo(o2));
    }

    @Test
    public void compareDirection() {
        var n = Direction.NORTH;
        var e = Direction.EAST;
        var s = Direction.SOUTH;
        var w = Direction.WEST;
        assertNotEquals(0, n.compareTo(e));
        assertNotEquals(0, n.compareTo(s));
        assertNotEquals(0, n.compareTo(w));
        assertNotEquals(0, e.compareTo(n));
        assertNotEquals(0, e.compareTo(s));
        assertNotEquals(0, e.compareTo(w));
        assertNotEquals(0, s.compareTo(n));
        assertNotEquals(0, s.compareTo(e));
        assertNotEquals(0, s.compareTo(w));
        assertNotEquals(0, w.compareTo(n));
        assertNotEquals(0, w.compareTo(n));
        assertNotEquals(0, w.compareTo(s));
    }
}
