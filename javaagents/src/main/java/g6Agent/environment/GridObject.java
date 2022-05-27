package g6Agent.environment;

import g6Agent.services.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unused
 */
public class GridObject {
    private static final String TAG = "Grid";
    private static Map<String, Point> listOfAllObstacles;

    public void setListOfAllObstacles(String identifier, Point pointerMarker) {
        if (listOfAllObstacles != null){
            listOfAllObstacles.put(identifier, pointerMarker);
        } else {
            listOfAllObstacles = new HashMap<>();
        }
    }
}
