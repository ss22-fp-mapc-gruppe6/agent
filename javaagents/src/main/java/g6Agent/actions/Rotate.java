package g6Agent.actions;

import eis.iilang.Action;
import g6Agent.services.Rotation;

public class Rotate extends Action implements G6Action{
    /**
     * Rotates the agent (and all attached things) 90 degrees in the given direction. For each attached thing, its final position after the rotation has to be free.
     * @param rotation the rotation direction (clockwise or counterclockwise).
     */
    public Rotate(Rotation rotation) {
        super("rotate", rotation.getIdentifier());
    }
}
