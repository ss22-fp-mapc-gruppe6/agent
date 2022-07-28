package g6Agent.perceptionAndMemory.Enties;

import g6Agent.services.Direction;
import g6Agent.services.Point;
import java.util.List;


/**
 * Record to store direction and speed of an movement
 *
 * @param directions the directions
 * @param speed      the speed
 *
 * @author Kai Müller
 */
public record Movement(List<Direction> directions, int speed) {


    public Point asVector() {
            Point p = new Point(0,0);
            int movementLeft = speed;
            for (int i = 0; i < directions.size(); i++){
                if (movementLeft > 0){
                     //move one field in the given direction
                        p = p.add(directions.get(i).getNextCoordinate());
                        movementLeft --;
                }
            }
            return p;
        }
}

