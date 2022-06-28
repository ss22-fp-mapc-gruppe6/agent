package g6Agent.decisionModule.astar;

import g6Agent.Tuple;
import g6Agent.actions.Clear;
import g6Agent.actions.G6Action;
import g6Agent.actions.Move;
import g6Agent.actions.Skip;
import g6Agent.decisionModule.PointAction;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AStar {

    public static G6Action astarNextStep(Point target, PerceptionAndMemory perceptionAndMemory) {
        return astarShortestPath(target, perceptionAndMemory).stream().findFirst().orElseGet(Skip::new);
    }

    public static List<G6Action> astarShortestPath(Point target, PerceptionAndMemory perceptionAndMemory) {
        final List<Block> directlyAttachedBlocks = perceptionAndMemory.getDirectlyAttachedBlocks();
        final List<Integer> movementSpeed = perceptionAndMemory.getCurrentRole().getMovementSpeed();
        final Integer stepSize = movementSpeed.get(directlyAttachedBlocks.size());
        return findShortestPath(
                new Point(0, 0),
                target,
                perceptionAndMemory.getObstacles(),
                stepSize,
                target::euclideanDistanceTo);
    }

    static List<G6Action> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize, Function<Point, Double> heuristic) {
        PriorityQueue<Wrapper> queue = new PriorityQueue<>(Wrapper::compareTo);

        HashMap<PointAction, Wrapper> wrappers = new HashMap<>();
        HashSet<Point> visited = new HashSet<>();
        final PointAction startPointAction = new PointAction(start, Move.class, start);
        final var startWrapper = Wrapper.create(startPointAction, null, 0.0, heuristic.apply(start), Set.of(), 0);
        wrappers.put(startPointAction, startWrapper);
        queue.add(startWrapper);

        while (!queue.isEmpty()) {
            final var currentWrapped = queue.poll();
            final var current = currentWrapped.pointAction();
            final Point currentPoint;
            if (Move.class.equals(current.action())) {
                currentPoint = current.to();
                visited.add(current.to());
            } else if (Clear.class.equals(current.action())) {
                currentPoint = current.from();
            } else {
                throw new IllegalStateException("should have been Move or Clear action");
            }
//            System.out.println("current = " + current + ", " + currentWrapped.costSum + ", " + currentWrapped.minimalRemainingCost);

            if (currentPoint.equals(target)) {
                final List<PointAction> path = currentWrapped.tracePath();

                final List<Point> points = path.stream().map(PointAction::from).toList();
                final var visualize = visualize(points, obstacles, start);
                System.out.println(visualize);
                final List<G6Action> g6Actions = actionsFromPointActions(path, stepSize);
                System.out.println("g6Actions = " + g6Actions);
                return g6Actions;
            }
            //            obstaclesAndVisited.addAll(visited);
            final var neighbours = getUnobstructedSteps(
                    obstacles.stream().filter(Predicate.not(currentWrapped.destroyedObstacles::contains)).collect(Collectors.toList()),
                    1,
                    currentPoint);
            for (var tuple : neighbours) {
                int step = currentWrapped.step();
                Point neighbour = tuple.a();
                final var action = tuple.b();
                final int cost;
                final Set<Point> destroyedObstacles;
                if (visited.contains(neighbour)) continue;
                if (action.equals(Move.class)) {
                    step++;
                    if (step == 1)
                        cost = 1;
                    else if (step <= stepSize)
                        cost = 0;
                    else {
                        step = 1;
                        cost = 1;
                    }
                    destroyedObstacles = Set.of();
                } else if (action.equals(Clear.class)) {
                    step = 0;
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
                                    destroyedObstacles, step);
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
                                    destroyedObstacles, step);

                    queue.add(replacement);

                }
            }
        }
        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return List.of();
    }

    private static List<G6Action> actionsFromPointActions(List<PointAction> path, int stepSize) {
        final List<G6Action> actions = new ArrayList<>();
        final LinkedList<PointAction> queue = new LinkedList<>();
        int step = 0;
        for (final PointAction pa : path) {
            final Class<? extends G6Action> action = pa.action();
            if (action.equals(Move.class)) {
                step++;
                queue.add(pa);
                if (step >= stepSize) {
                    step = 0;
                    actions.add(createMove(queue));
                    queue.clear();
                }
            } else if (action.equals(Clear.class)) {
                step = 0;
                //add queued partial move before clear
                if (!queue.isEmpty()) {
                    actions.add(createMove(queue));
                    queue.clear();
                }
                actions.add(new Clear(new Point(pa.to().x - pa.from().x, pa.to().y - pa.from().y)));
            }

        }
        //add partial move
        if (!queue.isEmpty()) actions.add(createMove(queue));
        return actions;
    }

    private static Move createMove(LinkedList<PointAction> pointActions) {
        return new Move(pointActions.stream()
                .sequential()
                .map(pa -> Direction.fromAdjacentPoint(
                        new Point(pa.to().x - pa.from().x, pa.to().y - pa.from().y)
                ))
                .toArray(Direction[]::new));
    }

    private static List<String> pointsToDirections(List<Point> queue) {
        //TODO
        return null;
    }

    static List<Tuple<Point, Class<? extends G6Action>>> getUnobstructedSteps(List<Point> obstacles, int reach, Point origin) {
        List<Tuple<Point, Class<? extends G6Action>>> directionsToGo = new ArrayList<>();
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

    static List<? extends G6Action> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize) {
        return findShortestPath(start, target, obstacles, stepSize, target::euclideanDistanceTo);
    }

    record Wrapper(PointAction pointAction, Wrapper predecessor, double costSum, double totalCostFromStart,
                   double minimalRemainingCost, Set<Point> destroyedObstacles,
                   int step) implements Comparable<Wrapper> {
        @Override
        public String toString() {
            return "(" + pointAction + ")-" + costSum;
        }

        static Wrapper create(PointAction pointAction, Wrapper predecessor, double totalCostFromStart, double minimumRemainingCost, Set<Point> destroyedObstacles, int step) {
            return new Wrapper(pointAction, predecessor, totalCostFromStart + minimumRemainingCost, totalCostFromStart, minimumRemainingCost, destroyedObstacles, step);
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

    static String visualize(List<Point> path, List<Point> obstacles, Point start) {
        path = new ArrayList<>(path);
        if (start != null) {
            path.add(0, start);
        }
        Optional<Point> maxXPoint = path.stream().max(Comparator.comparing(Point::getX));
        Optional<Point> maxYPoint = path.stream().max(Comparator.comparing(Point::getY));
        Optional<Point> minXPoint = path.stream().min(Comparator.comparing(Point::getX));
        Optional<Point> minYPoint = path.stream().min(Comparator.comparing(Point::getY));
        if (path.isEmpty())
            throw new IllegalArgumentException("list is empty");
        if (maxXPoint.isEmpty() || maxYPoint.isEmpty() || minXPoint.isEmpty() || minYPoint.isEmpty())
            throw new IllegalArgumentException("x or y dimension is empty?");
        int maxX = maxXPoint.get().x;
        int maxY = maxYPoint.get().y;
        int minX = minXPoint.get().x;
        int minY = minYPoint.get().y;

        Point target = path.get(path.size() - 1);


        String fielGap = " ";
        StringBuffer s = new StringBuffer();
        // x axis legend
        s.append(" ").append(" ").append(" ");
        for (int x = minX; x <= maxX; x++) {
            s.append(String.format("%5s", x));
        }
        s.append("\n");

        for (int y = minY; y <= maxY; y++) {
            //y axis legend
            s.append(String.format("%3s", y));

            for (int x = minX; x <= maxX; x++) {
                String field = " ";
                if (start != null && start.getX() == x && start.getY() == y)
                    field = "s";
                else if (target.getX() == x && target.getY() == y)
                    field = "t";
                else {
                    final var p = new Point(x, y);
                    if (path.contains(p))
                        field = String.valueOf(path.indexOf(p));
                    else if (obstacles.contains(p)) {
                        field = "b";
                    }
                }
                String fieldFormat = String.format("%5s", field);
                s.append(fieldFormat);
            }
            s.append("\n");
        }

        return s.toString();
    }

    static String visualize(List<Point> path, List<Point> obstacles) {
        return visualize(path, obstacles, null);
    }
}
