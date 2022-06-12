package g6Agent.environment;

import g6Agent.services.Point;

import java.util.concurrent.ConcurrentHashMap;

public class GridObject {
    private static final String TAG = "Grid";

    ConcurrentHashMap<Point, CellObject> cellMap = new ConcurrentHashMap<>();
   /** public BlockObject getBlockObjectAt(Point pAt) {
        CellObject cell = cellMap.get(pAt.getLimited());
        if (cell != null) {
            return cell.getBlockObject();
        }
        return null;
    }

     public CellObject getBlockObjectAt(Point attaching) {
        //:to do
    } */
}
