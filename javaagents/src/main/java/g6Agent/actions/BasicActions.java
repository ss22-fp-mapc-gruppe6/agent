package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Numeral;
import g6Agent.services.Direction;
import g6Agent.services.Rotation;


import java.awt.*;
import java.util.Random;

public class BasicActions {

    /**
     * Moves to a random Direction
     * @return the movement Action
     */
    public static Action moveRandomly(){
        Action action = null;
        int randomNumber = getRandomNumberInRange(0, 3);
        switch (randomNumber) {
            case 0 -> action = move(Direction.SOUTH);
            case 1 -> action = move(Direction.WEST);
            case 2 -> action = move(Direction.EAST);
            case 3 -> action = move(Direction.NORTH);
        }
        return action;
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

    }

    /** Skips this Step
     * @return the skip Action
     */
    public static Action skip(){
        return new Action("skip");
    }


    /**
     * Moves the Agent to te given Direction
     * @param direction the given Direction
     * @return the Movement Action
     */
    public static Action move(Direction direction){
        return new Action("move", direction.getIdentifier());
    }


    /**
     * Clears the Obstacle at the given Point, or disables an enemy Agent
     * @param point the point
     * @return the clear Action
     */
    public static Action clear(Point point) {return new Action("clear", new Numeral(point.x), new Numeral(point.y));
    }

    /**
     * Rotates the Agent with everything attached in the given Rotation
     * @param rotation the rotation
     * @return the rotation Action
     */
    public static Action rotate(Rotation rotation){
        return new Action("rotate", rotation.getIdentifier());
    }
}
