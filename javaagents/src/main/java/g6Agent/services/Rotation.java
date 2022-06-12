package g6Agent.services;

import eis.iilang.Identifier;

public enum Rotation {
    CLOCKWISE(new Identifier("cw")), COUNTERCLOCKWISE(new Identifier("ccw"));

    private final Identifier identifier;
    Rotation(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }


}
