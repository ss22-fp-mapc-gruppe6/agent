package g6Agent.perceptionAndMemory.Enties;

import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.List;


/**
 * Record to store direction and speed of an movement
 *
 * @param direction the direction
 * @param speed     the speed
 */
public record Movement(Direction direction, int speed) {


    /**
     * @return [(Identier) direction, (Numeral) speed]
     */
    public List<Parameter> asParameterList() {
        List<Parameter> list = new ArrayList<>();
        list.add(direction.getIdentifier());
        list.add(new Numeral(speed));

        return list;
    }

    public Point asVector() {
        return direction.getNextCoordinate().multiply(speed);
    }
}

