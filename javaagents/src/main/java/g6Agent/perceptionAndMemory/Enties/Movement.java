package g6Agent.perceptionAndMemory.Enties;

import g6Agent.services.Direction;
import g6Agent.services.Point;
import java.util.List;


/**
 * Record to store direction and speed of an movement
 *
 * @param directions the directions
 * @param speed     the speed
 *
 * @author Kai Müller
 */
public record Movement(List<Direction> directions, int speed) {


    public Point asVector() {
        if (directions.size() == 1){
            return directions.get(0).getNextCoordinate().multiply(speed);
        }else{
            Point p = new Point(0,0);
            int movementLeft = speed;
            for (int i = 0; i < directions.size(); i++){
                if (movementLeft > 0){
                    if(movementLeft > 1 && (i + 1) == directions.size()  ){ //case wants to move more than one field further with only one direction left
                        p = p.add(directions.get(i).getNextCoordinate().multiply(movementLeft));
                        movementLeft = 0;
                    } else { //move one field in the given direction
                        p = p.add(directions.get(i).getNextCoordinate());
                        movementLeft --;
                    }
                }
            }
            return p;
        }

    }
}

