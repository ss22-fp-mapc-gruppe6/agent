package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Numeral;
import g6Agent.services.Point;

public class Clear extends Action implements G6Action {
    /**
     * Clears an obstacle or disables an enemy agent at the given position
     * @param obstacle the position
     */
    public Clear(Point obstacle) {
        super("clear", new Numeral(obstacle.x), new Numeral(obstacle.y));
    }
}
