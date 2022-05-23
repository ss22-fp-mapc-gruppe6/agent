package g6Agent.services;


public class TestPoint {
    @org.junit.Test
    public void rotateTest(){
        assert(Direction.NORTH.rotate(Rotation.CLOCKWISE).getNextCoordinate().equals(Direction.EAST.getNextCoordinate()));
        assert (Direction.NORTH.getNextCoordinate().rotate(Rotation.CLOCKWISE).equals(Direction.EAST.getNextCoordinate()));
        assert (Direction.NORTH.getNextCoordinate().rotate(Rotation.COUNTERCLOCKWISE).equals(Direction.WEST.getNextCoordinate()));
    }
}
