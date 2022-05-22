package g6Agent.decissionModule;

import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.List;

public class AStar {
    record Node(Point p, int g, int h) {
    }

    public AStar() {
        System.out.println("AStar.AStar");
    }

    public List<Point> getPath(final Point target) {
        final Point start = new Point(0, 0);
        getAdjacent(start);
        final int i = start.manhattanDistanceTo(target);
        ArrayList<Point> path = new ArrayList<>();
        path.add(target);
        return path;
    }

    private List<Point> getAdjacent(Point start) {
        return null;
    }
}
