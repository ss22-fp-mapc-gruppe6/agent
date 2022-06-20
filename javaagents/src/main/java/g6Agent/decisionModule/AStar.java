package g6Agent.decisionModule;

import eis.iilang.Action;
import g6Agent.Tuple;
import g6Agent.actions.Clear;
import g6Agent.actions.Move;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AStar {

    public static List<PointAction> findShortestPath(Point target, List<Point> obstacles, int stepSize) {
        return findShortestPath(new Point(0, 0), target, obstacles, stepSize, target::euclideanDistanceTo);
    }

    public static List<PointAction> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize, Function<Point, Double> heuristic) {
        PriorityQueue<Wrapper> queue = new PriorityQueue<>(Wrapper::compareTo);

        HashMap<PointAction, Wrapper> wrappers = new HashMap<>();
        HashSet<Point> visited = new HashSet<>();
        final PointAction startPointAction = new PointAction(start, Move.class, start);
        final var startWrapper = Wrapper.create(startPointAction, null, 0.0, heuristic.apply(start), Set.of());
        wrappers.put(startPointAction, startWrapper);
        queue.add(startWrapper);

        while (!queue.isEmpty()) {
            final var currentWrapped = queue.poll();
            final var current = currentWrapped.pointAction();
            final Point currentPoint;
            if (Move.class.equals(current.action())) {
                currentPoint = current.to();
            } else if (Clear.class.equals(current.action())) {
                currentPoint = current.from();
            } else {
                throw new IllegalStateException("should have been Move or CLear action");
            }
            visited.add(current.to());
            System.out.println("current = " + current);

            if (currentPoint.equals(target)) {
                return currentWrapped.tracePath();
            }
            //            obstaclesAndVisited.addAll(visited);
            final var neighbours = getUnobstructedSteps(
                    obstacles.stream().filter(Predicate.not(currentWrapped.destroyedObstacles::contains)).collect(Collectors.toSet()),
                    stepSize,
                    currentPoint);
            for (var tuple : neighbours) {
                Point neighbour = tuple.a();
                final var action = tuple.b();
                final int cost;
                final Set<Point> destroyedObstacles;
                if (visited.contains(neighbour)) continue;
                if (action.equals(Move.class)) {
                    cost = 1;
                    destroyedObstacles = Set.of();
                } else if (action.equals(Clear.class)) {
                    cost = 1;
                    destroyedObstacles = Set.of(neighbour);
                } else
                    throw new IllegalStateException("unexpected action!");
                final PointAction pointAction = new PointAction(currentPoint, action, neighbour);
                var neighbourWrapped = wrappers.get(pointAction);
//                if (obstacles.contains(neighbour)) continue;

//                final int cost = current.manhattanDistanceTo(neighbour);
                final double totalCost = currentWrapped.totalCostFromStart() + cost;

                if (neighbourWrapped == null) {
                    final var minimumRemainingCost = heuristic.apply(neighbour);
                    neighbourWrapped =
                            Wrapper.create(
                                    pointAction,
                                    currentWrapped,
                                    totalCost,
                                    minimumRemainingCost,
                                    destroyedObstacles);
                    wrappers.put(pointAction, neighbourWrapped);
                    final var add = queue.add(neighbourWrapped);
                } else if (totalCost < neighbourWrapped.totalCostFromStart() || action.equals(Clear.class)) {
                    queue.remove(neighbourWrapped);

                    final var replacement =
                            Wrapper.create(
                                    pointAction,
                                    currentWrapped,
                                    totalCost,
                                    neighbourWrapped.minimalRemainingCost(),
                                    destroyedObstacles);

                    queue.add(replacement);

                }
            }
        }
        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return List.of();
    }

    static List<Tuple<Point, Class<? extends Action>>> getUnobstructedSteps(Set<Point> obstacles, int reach, Point origin) {
        List<Tuple<Point, Class<? extends Action>>> directionsToGo = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            final Point directionDelta = direction.getNextCoordinate();
            Point next = new Point(origin).add(directionDelta);
            int step = 0;
            if (obstacles.contains(next)) directionsToGo.add(new Tuple<>(new Point(next), Clear.class));
            else while (!obstacles.contains(next) && step++ < reach) {
                directionsToGo.add(new Tuple<>(new Point(next), Move.class));
                next = next.add(directionDelta);
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

    public static List<PointAction> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize) {
        return findShortestPath(start, target, obstacles, stepSize, target::euclideanDistanceTo);
    }

    record Wrapper(PointAction pointAction, AStar.Wrapper predecessor, double costSum, double totalCostFromStart,
                   double minimalRemainingCost, Set<Point> destroyedObstacles) implements Comparable<Wrapper> {
        @Override
        public String toString() {
            return "(" + pointAction + ")-" + costSum;
        }

        public static Wrapper create(PointAction pointAction, Wrapper predecessor, double totalCostFromStart, double minimumRemainingCost, Set<Point> destroyedObstacles) {
            return new Wrapper(pointAction, predecessor, totalCostFromStart + minimumRemainingCost, totalCostFromStart, minimumRemainingCost, destroyedObstacles);
        }

        @Override
        public int compareTo(Wrapper o) {
            final var i = Double.compare(this.costSum, o.costSum);
            return i;
        }


        List<PointAction> tracePath() {
            final List<PointAction> path = new LinkedList<>();
            var wrapper = this;
            while (wrapper.predecessor != null) {
//                path.add(new PointAction(wrapper.pointAction, wrapper.action));
                path.add(wrapper.pointAction);
                wrapper = wrapper.predecessor;
            }
            Collections.reverse(path);
            return path;
        }
    }
}
