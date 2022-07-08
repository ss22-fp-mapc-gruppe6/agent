package g6Agent.services;

import eis.iilang.Identifier;

public enum Direction {
    WEST(new Identifier("w"), new Point(-1, 0)), EAST(new Identifier("e"), new Point(1, 0)),
    NORTH(new Identifier("n"), new Point(0, -1)), SOUTH(new Identifier("s"), new Point(0, 1));

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
     *
     * @return the coordinate
     */
    public Point getNextCoordinate() {
        return nextCoordinate;
    }

    /**
     * Creates an Direction from a given Identifier
     *
     * @param id the Identifier
     * @return the Direction
     */
    public static Direction fromIdentifier(Identifier id) {
        Direction direction;
        switch (id.toProlog()) {
            case "w" -> direction = Direction.WEST;
            case "e" -> direction = Direction.EAST;
            case "n" -> direction = Direction.NORTH;
            case "s" -> direction = Direction.SOUTH;
            default -> throw new IllegalStateException(
                    "Unexpected value: " + id.toProlog()
                            + "in Direction.fromIdentifier()");
        }
        return direction;
    }

    public static Direction fromAdjacentPoint(Point p) {
        if (p.x == 0 && p.y == 1) return Direction.SOUTH;
        if (p.x == 1 && p.y == 0) return Direction.EAST;
        if (p.x == 0 && p.y == -1) return Direction.NORTH;
        if (p.x == -1 && p.y == 0) return Direction.WEST;
        throw new IllegalArgumentException("Point " + p + " was not adjacent as expected");
    }

    /**
     * @return a list of all possible directions
     */
    public static Direction[] allDirections() {
        return new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};
    }

    /**
     * returns a new direction, which is this direction, rotated in the given rotation
     *
     * @param rotation the rotation
     * @return the new direction
     */
    public Direction rotate(Rotation rotation) {
        if (rotation.equals(Rotation.CLOCKWISE)) {
                switch (this){
                    case WEST -> { return Direction.NORTH;}
                    case EAST -> { return Direction.SOUTH;}
                    case NORTH -> { return Direction.EAST;}
                    case SOUTH -> { return Direction.WEST; }
                }
            }
            else {
                switch (this){
                    case WEST -> { return Direction.SOUTH;}
                    case EAST -> { return Direction.NORTH;}
                    case NORTH -> {return Direction.WEST;}
                    case SOUTH -> {return Direction.EAST;}
                }
            }
        try {
            throw new Exception();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
