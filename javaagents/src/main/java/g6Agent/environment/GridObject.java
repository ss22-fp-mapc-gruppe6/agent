package g6Agent.environment;

import g6Agent.services.Point;

import java.util.concurrent.ConcurrentHashMap;

public class GridObject {
    private static final String TAG = "Grid";

    ConcurrentHashMap<Point, CellObject> cellMap = new ConcurrentHashMap<>();

     /**public CellObject getBlockObjectAt(Point attaching) {
        //:to do
    } */
}
