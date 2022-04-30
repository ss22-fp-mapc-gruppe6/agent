package g6Agent.services;

import eis.iilang.Identifier;

public enum Direction {
    WEST(new Identifier("w")), EAST(new Identifier("e")),
    NORTH(new Identifier("n")), SOUTH(new Identifier("s"));

    private final Identifier id;

    Direction(Identifier id) {
        this.id = id;
    }

    public Identifier getIdentifier() {
        return id;
    }
}
