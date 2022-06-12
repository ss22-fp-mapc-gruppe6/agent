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
            System.out.println("currentPoint = " + currentPoint);

            if (currentPoint.equals(target)) {
                return currentWrapped.tracePath();
            }
            final HashSet<Point> obstaclesAndVisited = new HashSet<>();
            obstaclesAndVisited.addAll(obstacles);
//            obstaclesAndVisited.addAll(visited);
            final var neighbours = getUnobstructedSteps(obstaclesAndVisited, stepSize, currentPoint, Steps.ALL_STEPS);
//            final var neighbours = getNeighbours(currentPoint);
            System.out.println("neighbours = " + neighbours);
            for (Point neighbour : neighbours) {
                if (obstacles.contains(neighbour)) continue;
                if (visited.contains(neighbour)) continue;

//                final int cost = currentPoint.manhattanDistanceTo(neighbour);
                final int cost = 1;
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
            System.out.println();
        }
        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return List.of();
    }

    static List<Point> getUnobstructedSteps(Set<Point> obstacles, int stepSize, Point origin, Steps steps) {
        List<Point> directionsToGo = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            final Point d = direction.getNextCoordinate();
            Point next = new Point(origin).add(d);
            int i = 0;
            Point success = null;
            switch (steps) {
                case MAX_STEP -> {
                    while (!obstacles.contains(next) && i++ < stepSize) {
                        success = new Point(next);
                        next = next.add(d);
                    }
                    if (success != null) {
                        directionsToGo.add(success);
                    }
                }
                case ALL_STEPS -> {
                    while (!obstacles.contains(next) && i++ < stepSize) {
                        success = new Point(next);
                        directionsToGo.add(success);
                        next = next.add(d);
                    }
                }
            }
        }
        return directionsToGo;
    }

    static List<Point> getUnobstructedStepsMax(Set<Point> obstacles, int stepSize) {
        return getUnobstructedStepsMax(obstacles, stepSize, new Point(0, 0));
    }

    enum Steps {
        MAX_STEP, ALL_STEPS
    }

    static List<Point> getUnobstructedStepsMax(Set<Point> obstacles, int stepSize, Point origin) {
        List<Point> directionsToGo = new ArrayList<>(4);
        for (Direction direction : Direction.allDirections()) {
            Point d = new Point(direction.getNextCoordinate());
            Point temp = new Point(d);

            Point success = null;
            int i = 0;
            while (!obstacles.contains(temp) && i++ < stepSize) {
                success = new Point(temp);
                temp = temp.add(d);
            }
            if (success != null) {
                directionsToGo.add(success);
            }
        }
        directionsToGo = directionsToGo.stream().map(e -> e.add(origin)).toList();
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
        @Override
        public String toString() {
            return "(" + point.x + "," + point.y + ")-" + costSum;
        }

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
