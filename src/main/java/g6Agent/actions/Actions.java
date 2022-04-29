package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import g6Agent.services.Direction;


import java.awt.*;
import java.util.Random;

public class Actions {

    /**
     * Moves to a random Direction
     * @return the movement Action
     */
    public static Action moveRandomly(){
        Action action = null;
        int randomNumber = getRandomNumberInRange(0, 3);
        switch (randomNumber) {
            case 0: action = move(Direction.SOUTH); break;
            case 1: action = move(Direction.WEST); break;
            case 2: action = move(Direction.EAST); break;
            case 3: action = move(Direction.NORTH); break;
        }
        return action;
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

    }



    /**
     * Moves the Agent to te given Direction
     * @param direction the given Direction
     * @return the Movement Action
     */
    public static Action move(Direction direction){
        Action action = null;
        switch (direction) {
            case SOUTH: action = new Action("move", new Identifier("s")); break;
            case WEST: action = new Action("move", new Identifier("w")); break;
            case NORTH: action = new Action("move", new Identifier("n")); break;
            case EAST: action = new Action("move", new Identifier("e")); break;
        }
        return action;
    }


    /**
     * Clears the Obstacle at the given Point, or disables an enemy Agent
     * @param point
     * @return the clear Action
     */
    public static Action clear(Point point) {return new Action("clear", new Numeral(point.x), new Numeral(point.y));
    }
}
