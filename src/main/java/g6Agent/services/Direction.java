package g6Agent.services;

import eis.iilang.Identifier;

public enum Direction {
    WEST(new Identifier("w"), new Point(-1,0)), EAST(new Identifier("e"), new Point(1,0)),
    NORTH(new Identifier("n"), new Point(0,-1)), SOUTH(new Identifier("s"), new Point(0,1));

    private final Identifier id;
    private final Point nextCoordinate;

    Direction(Identifier id, Point nextCoordinate) {
        this.id = id;
        this.nextCoordinate = nextCoordinate;
    }

    /**
     * @return the Identifier of this Direction
     */
    public Identifier getIdentifier() {
        return id;
    }

    /**
     * Gives the next coordinate in this direction, for example (1,0) for East
     * @return the coordinate
     */
    public Point getNextCoordinate() {
        return nextCoordinate;
    }
}
