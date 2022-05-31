package g6Agent.decisionModule;

import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

public class AStar {


    public static List<Point> findShortestPath(Point target, List<Point> obstacles, int stepSize) {
        return findShortestPath(new Point(0, 0), target, obstacles, stepSize, target::euclideanDistanceTo);
    }

    public static List<Point> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize, Function<Point, Double> heuristic) {
        PriorityQueue<Wrapper> queue = new PriorityQueue<>(Wrapper::compareTo);

        HashMap<Point, Wrapper> wrappers = new HashMap<>();
        HashSet<Point> visited = new HashSet<>();
        final var startWrapper = Wrapper.create(start, null, 0.0, heuristic.apply(start));
        wrappers.put(start, startWrapper);
        queue.add(startWrapper);

        while (!queue.isEmpty()) {
            final var currentWrapped = queue.poll();
            final var currentPoint = currentWrapped.point();
            visited.add(currentPoint);

            if (currentPoint.equals(target)) {
                return currentWrapped.tracePath();
            }
            getUnobstructedDirections(obstacles);
            final var neighbours = getNeighbours(currentPoint);
            for (Point neighbour : neighbours) {
                if (obstacles.contains(neighbour)) continue;
                if (visited.contains(neighbour)) continue;

                double cost = 1;
                final double totalCost = currentWrapped.totalCostFromStart() + cost;

                var neighbourWrapped = wrappers.get(neighbour);
                if (neighbourWrapped == null) {
                    final var minimumRemainingCost = heuristic.apply(neighbour);
                    neighbourWrapped = Wrapper.create(neighbour, currentWrapped, totalCost, minimumRemainingCost);
                    wrappers.put(neighbour, neighbourWrapped);
                    final var add = queue.add(neighbourWrapped);
                } else if (totalCost < neighbourWrapped.totalCostFromStart()) {
                    queue.remove(neighbourWrapped);

                    final var replacement = Wrapper.create(neighbourWrapped.point(), currentWrapped, totalCost, neighbourWrapped.minimalRemainingCost());

                    queue.add(replacement);

                }
            }
        }
        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return List.of();
    }

    static ArrayList<Point> getUnobstructedDirections(List<Point> obstacles) {
        final var directionsToGo = new ArrayList<Point>(4);
        for (Direction direction : Direction.values()) {
            final Point d = direction.getNextCoordinate();
            // either 0 or 1
            final var dx = d.x;
            final var dy = d.y;

            final var temp = new Point(d.x, d.y);
            for (int i = 0;
                 i < 3 && !obstacles.contains(temp);
                 i++, temp.translate(dx, dy)
            ) {
                directionsToGo.add(temp);
            }
        }
        return directionsToGo;
    }

    static Collection<Point> getNeighbours(Point p) {
        return Set.of(new Point(p.x, p.y + 1), new Point(p.x, p.y - 1), new Point(p.x + 1, p.y), new Point(p.x - 1, p.y));
    }

    public static List<Point> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize) {
        return findShortestPath(start, target, obstacles, stepSize, target::euclideanDistanceTo);
    }

    public static List<Direction> directionsFrom(List<Point> points) {
        Point previous = new Point(0, 0);
        List<Direction> directions = new LinkedList<>();
        for (Point point : points) {
            int x = point.x - previous.x;
            int y = point.y - previous.y;
            final var direction = Direction.fromAdjacentPoint(new Point(x, y));
            directions.add(direction);
            previous = point;
        }
        return directions;
    }

    record Wrapper(Point point, AStar.Wrapper predecessor, double costSum, double totalCostFromStart,
                   double minimalRemainingCost) implements Comparable<Wrapper> {

        public static Wrapper create(Point point, Wrapper predecessor, double totalCostFromStart, double minimumRemainingCost) {
            return new Wrapper(point, predecessor, totalCostFromStart + minimumRemainingCost, totalCostFromStart, minimumRemainingCost);
        }

        @Override
        public int compareTo(Wrapper o) {
            final var i = Double.compare(this.costSum, o.costSum);
            return i;
        }


        List<Point> tracePath() {
            final var path = new LinkedList<Point>();
            var wrapper = this;
            while (wrapper.predecessor != null) {
                path.add(wrapper.point);
                wrapper = wrapper.predecessor;
            }
            Collections.reverse(path);
            return path;
        }
    }
}
