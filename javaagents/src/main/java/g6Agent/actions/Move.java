package g6Agent.actions;

import eis.iilang.Action;

import g6Agent.services.Direction;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Agent moves in the specified direction (north, west, est south)
 */

public class Move extends Action implements G6Action {

    /**
     * Moves the agent in the specified directions. If the agent is currently allowed to move more than one cell, multiple directions can be given.
     *
     * @param directions the directions to move
     */

    public Move(Direction... directions) {
        super("move", Arrays.stream(directions).map(Direction::getIdentifier).collect(Collectors.toList()));
    }
}
