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

    /**
     * Creates an Direction from a given Identifier
     * @param id the Identifier
     * @return the Direction
     */
    public static Direction fromIdentifier(Identifier id){
        Direction direction;
        switch (id.toProlog()) {
            case "w" ->  direction = Direction.WEST;
            case "e" -> direction = Direction.EAST;
            case "n" -> direction = Direction.NORTH;
            case "s" -> direction = Direction.SOUTH;
            default ->
                    throw new IllegalStateException(
                            "Unexpected value: " + id.toProlog()
                                    + "in Direction.fromIdentifier()");
        }
        return direction;
    }
}
