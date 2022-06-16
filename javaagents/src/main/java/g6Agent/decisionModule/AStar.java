package g6Agent.decisionModule;

import eis.iilang.Action;
import g6Agent.Tuple;
import g6Agent.actions.Clear;
import g6Agent.actions.Move;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.*;
import java.util.function.Function;

public class AStar {


    public static List<Tuple<Point, Class<? extends Action>>> findShortestPath(Point target, List<Point> obstacles, int stepSize) {
        return findShortestPath(new Point(0, 0), target, obstacles, stepSize, target::euclideanDistanceTo);
    }

    public static List<Tuple<Point, Class<? extends Action>>> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize, Function<Point, Double> heuristic) {
        PriorityQueue<Wrapper> queue = new PriorityQueue<>(Wrapper::compareTo);

        HashMap<Point, Wrapper> wrappers = new HashMap<>();
        HashSet<Point> visited = new HashSet<>();
        final var startWrapper = Wrapper.create(start, null, 0.0, heuristic.apply(start), Move.class);
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
            //            obstaclesAndVisited.addAll(visited);
            final var neighbours = getUnobstructedSteps(new HashSet<>(obstacles), stepSize, currentPoint);
            for (var tuple : neighbours) {
                final Point neighbour = tuple.a();
                final var action = tuple.b();
                final int cost;
                if (action.equals(Move.class))
                    cost = 1;
                else if (action.equals(Clear.class))
                    cost = 3;
                else
                    throw new IllegalStateException("unexpected action!");
//                if (obstacles.contains(neighbour)) continue;
                if (visited.contains(neighbour)) continue;

//                final int cost = currentPoint.manhattanDistanceTo(neighbour);
                final double totalCost = currentWrapped.totalCostFromStart() + cost;

                var neighbourWrapped = wrappers.get(neighbour);
                if (neighbourWrapped == null) {
                    final var minimumRemainingCost = heuristic.apply(neighbour);
                    neighbourWrapped = Wrapper.create(neighbour, currentWrapped, totalCost, minimumRemainingCost, action);
                    wrappers.put(neighbour, neighbourWrapped);
                    final var add = queue.add(neighbourWrapped);
                } else if (totalCost < neighbourWrapped.totalCostFromStart()) {
                    queue.remove(neighbourWrapped);

                    final var replacement = Wrapper.create(neighbourWrapped.point(), currentWrapped, totalCost, neighbourWrapped.minimalRemainingCost(), action);

                    queue.add(replacement);

                }
            }
            System.out.println();
        }
        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return List.of();
    }

    static List<Tuple<Point, Class<? extends Action>>> getUnobstructedSteps(Set<Point> obstacles, int stepSize, Point origin) {
        List<Tuple<Point, Class<? extends Action>>> directionsToGo = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            final Point d = direction.getNextCoordinate();
            Point next = new Point(origin).add(d);
            int i = 0;
            Class<? extends Action> aClass;
            if (!obstacles.contains(next)) aClass = Move.class;
            else aClass = Clear.class;
            while (!obstacles.contains(next) && i++ < stepSize) {
                directionsToGo.add(new Tuple<>(new Point(next), aClass));
                next = next.add(d);
            }
        }
        return directionsToGo;
    }

    static List<Point> getUnobstructedStepsMax(Set<Point> obstacles, int stepSize) {
        return getUnobstructedStepsMax(obstacles, stepSize, new Point(0, 0));
    }

    enum ReturnMode {
        MAX_STEP, ALL_STEPS, ALL_AND_IMMEDIATE_OBSTACLE
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

    public static List<Tuple<Point, Class<? extends Action>>> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize) {
        return findShortestPath(start, target, obstacles, stepSize, target::euclideanDistanceTo);
    }

    record Wrapper(Point point, AStar.Wrapper predecessor, double costSum, double totalCostFromStart,
                   double minimalRemainingCost, Class<? extends Action> action) implements Comparable<Wrapper> {
        @Override
        public String toString() {
            return "(" + point.x + "," + point.y + ")-" + costSum;
        }

        public static Wrapper create(Point point, Wrapper predecessor, double totalCostFromStart, double minimumRemainingCost, Class<? extends Action> action) {
            return new Wrapper(point, predecessor, totalCostFromStart + minimumRemainingCost, totalCostFromStart, minimumRemainingCost, action);
        }

        @Override
        public int compareTo(Wrapper o) {
            final var i = Double.compare(this.costSum, o.costSum);
            return i;
        }


        List<Tuple<Point, Class<? extends Action>>> tracePath() {
            final var path = new LinkedList<Tuple<Point, Class<? extends Action>>>();
            var wrapper = this;
            while (wrapper.predecessor != null) {
                path.add(new Tuple<>(wrapper.point, wrapper.action));
                wrapper = wrapper.predecessor;
            }
            Collections.reverse(path);
            return path;
        }
    }
}
