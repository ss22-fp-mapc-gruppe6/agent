package g6Agent.actions;

import eis.iilang.Action;

import g6Agent.services.Direction;

/**
 * Agent moves in the specified direction (north, west, est south)
 */

public class Move extends Action implements G6Action {

    /**
     * Moves the agent in the specified directions. If the agent is currently allowed to move more than one cell, multiple directions can be given.
     *
     * @param direction the direction to move
     */

    public Move(Direction direction) {
        super("move", direction.getIdentifier());
    }
}
