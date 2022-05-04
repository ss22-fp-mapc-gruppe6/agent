package g6Agent.perceptionAndMemory.AgentMap;

import eis.iilang.Numeral;
import eis.iilang.Parameter;
import g6Agent.services.Direction;

import java.util.ArrayList;
import java.util.List;


/**
 * Record to store direction and speed of an movement
 * @param direction the direction
 * @param speed the speed
 */
public record Movement(Direction direction, int speed) {

    /**
     *
     * @return [(Identier) direction, (Numeral) speed]
     */
    public List<Parameter> asParameterList(){
        List<Parameter> list = new ArrayList<>();
        list.add(direction.getIdentifier());
        list.add(new Numeral(speed));

        return list;
    }
}

