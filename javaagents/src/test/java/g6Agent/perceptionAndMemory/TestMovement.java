package g6Agent.perceptionAndMemory;

import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import org.junit.Test;

import java.util.List;

public class TestMovement {

    @Test
    public void movementTest(){
        Movement p0 = new Movement(List.of(Direction.EAST), 2);
        assert (p0.asVector().equals(Direction.EAST.getNextCoordinate().multiply(2)));

        Movement p1 = new Movement(List.of(Direction.EAST, Direction.SOUTH), 2);
        assert (p1.asVector().equals(Direction.EAST.getNextCoordinate().add(Direction.SOUTH.getNextCoordinate())));

        Movement p2 = new Movement(List.of(Direction.EAST, Direction.SOUTH), 0);
        assert (p2.asVector().equals(new Point(0,0)));

        Movement p3 = new Movement(List.of(Direction.EAST, Direction.WEST), 2);
        assert (p3.asVector().equals(new Point(0,0)));

        Movement p4 = new Movement(List.of(Direction.EAST, Direction.NORTH), 3);
        assert (p4.asVector().equals(Direction.EAST.getNextCoordinate().add(Direction.NORTH.getNextCoordinate().multiply(2))));

        Movement p5 = new Movement(List.of(Direction.EAST, Direction.EAST, Direction.SOUTH), 3);
        assert (p5.asVector().equals(Direction.EAST.getNextCoordinate().multiply(2).add(Direction.SOUTH.getNextCoordinate())));

        Movement p6 =  new Movement(List.of(Direction.EAST, Direction.NORTH), 1);
        assert (p6.asVector().equals(Direction.EAST.getNextCoordinate()));
    }
}
