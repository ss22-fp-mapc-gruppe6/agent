package g6Agent.decisionModule;

import g6Agent.services.Point;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AStar {

    public static final BiFunction<Point, Point, Double> h = Point::euclideanDistanceTo;
    public static final BiFunction<Point, Point, Integer> g = Point::manhattanDistanceTo;

    Point start = new Point(0, 0);
    Point target;
    HashSet<Wrapper> closed = new HashSet<>();

    public static List<Point> findShortestPath(Point start, Point target, Set<Point> obstacles) {
        return findShortestPath(start, target, obstacles, target::euclideanDistanceTo);
    }

    public static List<Point> findShortestPath(Point start, Point target, Set<Point> obstacles, Function<Point, Double> heuristic) {
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

        return List.of();
    }


    static Collection<Point> getNeighbours(Point p) {
        return Set.of(new Point(p.x, p.y + 1), new Point(p.x, p.y - 1), new Point(p.x + 1, p.y), new Point(p.x - 1, p.y));
    }


}
