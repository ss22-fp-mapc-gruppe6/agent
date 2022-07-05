package g6Agent.decisionModule.astar;

import g6Agent.Tuple;
import g6Agent.actions.*;
import g6Agent.decisionModule.PointAction;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static g6Agent.services.Direction.*;
import static g6Agent.services.Rotation.CLOCKWISE;
import static g6Agent.services.Rotation.COUNTERCLOCKWISE;

public class AStar {

    public static G6Action astarNextStep(Point target, PerceptionAndMemory perceptionAndMemory) {
        return astarShortestPath(target, perceptionAndMemory).stream().findFirst().orElseGet(Skip::new);
    }

    public static List<G6Action> astarShortestPath(Point target, PerceptionAndMemory perceptionAndMemory) {
        final List<Block> directlyAttachedBlocks = perceptionAndMemory.getDirectlyAttachedBlocks();
        final List<Integer> movementSpeed = perceptionAndMemory.getCurrentRole().getMovementSpeed();
        final Integer stepSize = movementSpeed.get(directlyAttachedBlocks.size());
        return findShortestPath(new Point(0, 0), target, perceptionAndMemory.getObstacles(), stepSize, target::euclideanDistanceTo, directlyAttachedBlocks.stream().map(Block::getCoordinates).toList());
    }

    static List<G6Action> findShortestPath(Point start, Point target, List<Point> obstacles, int stepSize, Function<Point, Double> heuristic, List<Point> attachments) {
        PriorityQueue<Wrapper> queue = new PriorityQueue<>(Wrapper::compareTo);

        HashMap<PointAction, Wrapper> wrappers = new HashMap<>();
        HashSet<Tuple<Point, Direction>> visited = new HashSet<>();
        final PointAction startPointAction = new PointAction(start, new Move(), start);
        final var startWrapper = new Wrapper(startPointAction, null, 0.0, 0.0, heuristic.apply(start), Set.of(), 0, attachments, Direction.NORTH, new Stack<>());
        wrappers.put(startPointAction, startWrapper);
        queue.add(startWrapper);

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            final G6Action action = current.pointAction.action();
            final Point currentLocation;
            visited.add(new Tuple<>(current.pointAction.location(), current.compass));
            switch (action) {
                case Move move -> {
                        currentLocation = current.pointAction.target();
                }
                case Clear clear -> {
                    currentLocation = current.pointAction.location();
                    current.destroyedObstacles.add(current.pointAction.target());
                    System.out.println("currentLocation = " + currentLocation);
                    System.out.println("clear = " + clear);
                }
                case Rotate rotate -> {
                    try {
                        rotate.predictSuccess(attachments, obstacles);
                        for (Point p : current.attachments) {
                            p.rotate(rotate.rotation);
                        }
                        Wrapper pop = current.postponed.pop();
                        wrappers.put(pop.pointAction, pop);
                        //we've been here before, skip to postponed wrapper
                        continue;
                    } catch (Rotate.AttachmentCollidingWithObstacleException e) {
                        //TODO go deeper
//                        shouldHaveRotatedEarlier(wrappers, current, queue);
                        continue;
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + action);
            }

            if (currentLocation.equals(target)) {
                return traceResult(start, obstacles, stepSize, current);
            }
            List<Point> c1 = new ArrayList<>(obstacles.stream().filter(Predicate.not(current.destroyedObstacles::contains)).toList());
            c1.sort(Point.pointComparator);
            final var adjacentActions = getNextActions(
                    c1,
                    currentLocation
            );
            List<Wrapper> nextStep = new ArrayList<>();
            for (Map.Entry<Point, G6Action> kv : adjacentActions.entrySet()) {
                G6Action nextAction = kv.getValue();
                int step = current.step;
                final int cost;
                final Set<Point> destroyedObstacles = new HashSet<>(current.destroyedObstacles);
                Point nextTarget = kv.getKey();
                switch (nextAction) {
                    case Move m -> {
                        try {
                            m.predictSuccess(attachments, obstacles);
                        } catch (Move.AttachmentCollidingWithObstacleException e) {
                            current.postponed.push(current);
                            shouldHaveRotatedEarlier(wrappers, current, queue);
                            continue;
                        }
                        step++;
                        if (step == 1) cost = 1;
                        else if (step <= stepSize) cost = 0;
                        else {
                            step = 1;
                            cost = 1;
                        }
                    }
                    case Clear c -> {
                        step = 0;
                        cost = 2;
                        destroyedObstacles.add(nextTarget);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + kv);
                }
                final PointAction pointAction = new PointAction(currentLocation, nextAction, nextTarget);
                final double totalCost = current.totalCostFromStart + cost;
                final double minimumRemainingCost = heuristic.apply(nextTarget);

                Wrapper nextWrapped = wrappers.get(pointAction);
                if (nextWrapped == null) {
                    nextWrapped = current
                            .withPointAction(pointAction)
                            .withPredecessor(current)
                            .withCostSum(totalCost + minimumRemainingCost)
                            .withTotalCostFromStart(totalCost)
                            .withMinimalRemainingCost(minimumRemainingCost)
                            .withDestroyedObstacles(destroyedObstacles)
                            .withStep(step);
                    wrappers.put(pointAction, nextWrapped);
                    nextStep.add(nextWrapped);
                } else if (totalCost < nextWrapped.totalCostFromStart) {
                    queue.remove(nextWrapped);
                    Wrapper replacement = nextWrapped
                            .withPointAction(pointAction)
                            .withPredecessor(current)
                            .withCostSum(totalCost + nextWrapped.minimalRemainingCost)
                            .withTotalCostFromStart(totalCost)
                            .withDestroyedObstacles(destroyedObstacles)
                            .withStep(step)
                            .withCompass(NORTH)
                            .withPostponed(new Stack<>());
                    nextStep.add(replacement);
                }
            }
            queue.addAll(nextStep);
        }
        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return List.of();
    }

    private static void shouldHaveRotatedEarlier(HashMap<PointAction, Wrapper> wrappers, Wrapper current, PriorityQueue<Wrapper> queue) {
        Rotate cw = new Rotate(CLOCKWISE);
        Rotate ccw = new Rotate(COUNTERCLOCKWISE);

        Wrapper predecessor = current.predecessor;
        Point location = predecessor.pointAction.location();
        PointAction key1 = new PointAction(location, cw, location);
        current.postponed.push(current);
        Wrapper w1 = current
                .withPointAction(key1)
                .withTotalCostFromStart(predecessor.totalCostFromStart + 1)
                .withCostSum(predecessor.totalCostFromStart + 1 + predecessor.minimalRemainingCost);
        wrappers.put(key1, w1);
        PointAction key2 = new PointAction(location, ccw, location);
        Wrapper w2 = current
                .withPointAction(key2)
                .withTotalCostFromStart(predecessor.totalCostFromStart + 1)
                .withCostSum(predecessor.totalCostFromStart + 1 + predecessor.minimalRemainingCost);
        wrappers.put(key2, w2);
        queue.add(w1);
        queue.add(w2);
    }

    @NotNull
    private static List<G6Action> traceResult(Point start, List<Point> obstacles, int stepSize, Wrapper currentWrapped) {
        final List<PointAction> path = currentWrapped.tracePath();

        final List<Point> points = path.stream().map(PointAction::location).toList();
        final var visualize = visualize(points, obstacles, start);
        System.out.println(visualize);
        final List<G6Action> g6Actions = actionsFromPointActions(path, stepSize);
        System.out.println("g6Actions = " + g6Actions);
        return g6Actions;
    }

    private static List<G6Action> actionsFromPointActions(List<PointAction> path, int stepSize) {
        final List<G6Action> actions = new ArrayList<>();
        final LinkedList<PointAction> queue = new LinkedList<>();
        int step = 0;
        for (final PointAction pa : path) {
            final G6Action action = pa.action();
            switch (action) {
                case Move move -> {
                    step++;
                    queue.add(pa);
                    if (step >= stepSize) {
                        step = 0;
                        actions.add(bundle(queue));
                        queue.clear();
                    }
                }
                case Clear clear -> {
                    step = 0;
                    //add queued partial move before clear
                    if (!queue.isEmpty()) {
                        actions.add(bundle(queue));
                        queue.clear();
                    }
                    actions.add(clear);
                }
                default -> throw new IllegalStateException("Unexpected value: " + action);
            }
        }
        //add partial move
        if (!queue.isEmpty()) actions.add(bundle(queue));
        return actions;
    }


    private static Move bundle(LinkedList<PointAction> pointActions) {
        return new Move(pointActions.stream().sequential()
                .map(PointAction::action)
                .map(g6Action -> ((Move) g6Action))
                .flatMap(move -> move.directions.stream().sequential())
                .toArray(Direction[]::new)
        );
    }

    private static List<String> pointsToDirections(List<Point> queue) {
        //TODO
        return null;
    }

    static Map<Point, G6Action> getNextActions(List<Point> obstacles, Point origin) {
        Map<Point, G6Action> result = new HashMap<>(4);
        for (Direction d : Direction.values()) {
            Point relative = d.getNextCoordinate();
            Point absolute = origin.add(relative);
            if (obstacles.contains(absolute))
                result.put(absolute, new Clear(relative));
            else
                result.put(absolute, new Move(d));
        }
        return result;
    }

    @With
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    static final class Wrapper implements Comparable<Wrapper> {
        final PointAction pointAction;
        final Wrapper predecessor;
        final double costSum;
        final double totalCostFromStart;
        final double minimalRemainingCost;
        final Set<Point> destroyedObstacles;
        final int step;
        final List<Point> attachments;
        final Direction compass;
        final Stack<Wrapper> postponed;

        @Override
        public String toString() {
            return "(" + pointAction.action() + ")-" + costSum;
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
        if (path.isEmpty()) throw new IllegalArgumentException("list is empty");
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
                if (start != null && start.getX() == x && start.getY() == y) field = "s";
                else if (target.getX() == x && target.getY() == y) field = "t";
                else {
                    final var p = new Point(x, y);
                    if (path.contains(p)) field = String.valueOf(path.indexOf(p));
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
