package g6Agent.decisionModule.astar;

import g6Agent.ComparableTuple;
import g6Agent.actions.*;
import g6Agent.decisionModule.PointAction;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static g6Agent.services.Rotation.CLOCKWISE;
import static g6Agent.services.Rotation.COUNTERCLOCKWISE;

@RequiredArgsConstructor
@Log
public class AStarReimplemented {
    final Point start;
    final Point target;
    final int stepSize;
    final List<Point> attachments;
    final Set<Point> pointsInMyWay;
    final Set<ComparableTuple<PointAction, Direction>> visited = new TreeSet<>();
    volatile Queue<Wrapper> queue = new PriorityQueue<>(Wrapper::compareTo);
    PointAction startPointAction;
    Wrapper startWrapper;

    public static G6Action astarNextStep(Point target, PerceptionAndMemory perceptionAndMemory) {
        return astarShortestPath(target, perceptionAndMemory).stream().findFirst().orElseGet(Skip::new);
    }

    public static List<G6Action> astarShortestPath(Point target, PerceptionAndMemory perceptionAndMemory) {
        final List<Point> directlyAttachedBlocks = perceptionAndMemory.getDirectlyAttachedBlocks().stream().map(Block::getCoordinates).toList();
        final List<Integer> movementSpeed = perceptionAndMemory.getCurrentRole().getMovementSpeed();
        final Integer stepSize = movementSpeed.get(directlyAttachedBlocks.size());
        final List<Point> blockingBlocks = perceptionAndMemory.getBlocks().stream().map(Block::getCoordinates).filter(Predicate.not(directlyAttachedBlocks::contains)).toList();
        TreeSet<Point> pointsInMyWay = Stream.concat(perceptionAndMemory.getObstacles().stream(), blockingBlocks.stream()).collect(Collectors.toCollection(TreeSet::new));
        AStarReimplemented aStar = new AStarReimplemented(new Point(0, 0), target, stepSize, directlyAttachedBlocks, pointsInMyWay);
        List<G6Action> shortestPath = aStar.findShortestPath();
        return shortestPath;
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

    static List<G6Action> actionsFromPointActions(List<PointAction> path, int stepSize) {
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
                case Rotate rotate -> {
                    step = 0;
                    //add queued partial move before clear
                    if (!queue.isEmpty()) {
                        actions.add(bundle(queue));
                        queue.clear();
                    }
                    actions.add(rotate);
                }
                default -> throw new IllegalStateException("Unexpected value: " + action);
            }
        }
        //add partial move
        if (!queue.isEmpty()) actions.add(bundle(queue));
        return actions;
    }

    static Move bundle(LinkedList<PointAction> pointActions) {
        return new Move(pointActions.stream().sequential()
                .map(PointAction::action)
                .map(g6Action -> ((Move) g6Action))
                .flatMap(move -> move.directions.stream().sequential())
                .toArray(Direction[]::new)
        );
    }

    List<G6Action> findShortestPath() {
        startPointAction = new PointAction(start, new Move(), start);
        startWrapper = new Wrapper(startPointAction, null, 0.0, 0.0, target.euclideanDistanceTo(start), Set.of(), 0, attachments, Direction.NORTH, new Stack<>());
        queue.add(startWrapper);

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            if (current.pointAction.location().equals(target)) {
                log.info("############target reached################");
                List<G6Action> g6Actions = traceResult(this.start, pointsInMyWay, stepSize, current.predecessor == null ? current : current.predecessor);
                return g6Actions;
            }
            List<Wrapper> nextSteps = getNextSteps(current);
            queue.addAll(nextSteps);
        }

        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return null;
    }

    List<Wrapper> getNextSteps(Wrapper current) {
        ComparableTuple<PointAction, Direction> pointAndDirection = new ComparableTuple<>(current.pointAction, current.compass);
        if (visited.contains(pointAndDirection)) {
            return List.of();
        }

        G6Action action = current.pointAction.action();
        final Point currentLocation;
        switch (action) {
            case Move move -> {
                try {
                    move.predictSuccess(
                            current.attachments
                                    .stream().map(p -> p.add(current.pointAction.location())).toList(),
                            obstaclesWithoutDestroyedAt(current)
                                    .stream().map(p -> p.add(current.pointAction.location())).toList()
                    );
                } catch (Move.AttachmentCollidingWithObstacleException e) {
                    return shouldHaveRotatedEarlier(current, e);
                }
                currentLocation = current.pointAction.target();
            }
            case Clear clear -> {
                currentLocation = current.pointAction.location();
                current.destroyedObstacles.add(current.pointAction.target());
            }
            case Rotate rotate -> {
                try {
                    rotate.predictSuccess(
                            current.attachments
                                    .stream().map(p -> p.add(current.pointAction.location())).toList(),
                            obstaclesWithoutDestroyedAt(current)
                                    .stream().map(p -> p.add(current.pointAction.location())).toList());
                    current.attachments = current.attachments.stream().map(point -> point.rotate(rotate.rotation)).toList();
                    currentLocation = current.pointAction.location();
                } catch (Rotate.AttachmentCollidingWithObstacleException e) {
                    return shouldHaveClearedEarlier(current, e);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + action);
        }
        visited.add(pointAndDirection);

        final var adjacentActions = getNextActions(
                obstaclesWithoutDestroyedAt(current),
                currentLocation
        );

        List<Wrapper> nextSteps = new ArrayList<>();
        for (Map.Entry<Point, G6Action> kv : adjacentActions.entrySet()) {
            G6Action nextAction = kv.getValue();
            int step = current.step;
            final int cost;
            final Set<Point> destroyedObstacles = new HashSet<>(current.destroyedObstacles);
            Point nextTarget = kv.getKey();
            switch (nextAction) {
                case Move m -> {
                    if (current.pointAction.action() instanceof Move && current.pointAction.location().equals(nextTarget)) {
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
                    cost = 1;
                    destroyedObstacles.add(nextTarget);
                }
                default -> throw new IllegalStateException("Unexpected value: " + kv);
            }
            final PointAction pointAction = new PointAction(currentLocation, nextAction, nextTarget);
            final double totalCost = current.totalCostFromStart + cost;
            final double minimumRemainingCost = nextTarget.euclideanDistanceTo(target);
            Wrapper nextWrapped = current
                    .withPointAction(pointAction)
                    .withPredecessor(current)
                    .withCostSum(totalCost + minimumRemainingCost)
                    .withTotalCostFromStart(totalCost)
                    .withMinimalRemainingCost(minimumRemainingCost)
                    .withDestroyedObstacles(destroyedObstacles)
                    .withStep(step);
            nextSteps.add(nextWrapped);
        }
        return nextSteps;
    }

    private List<Wrapper> shouldHaveClearedEarlier(Wrapper current, Rotate.AttachmentCollidingWithObstacleException e) {
        Point collision = e.getCollision().add(current.pointAction.location().invert());

        Wrapper destroyedWrapper = current
                .withPointAction(new PointAction(current.getPointAction().location(), new Clear(collision), collision ))
                .withPredecessor(current)
                .withCostSum(current.costSum-1)   //+1 for clear action -1 for having freed up a space
                .withTotalCostFromStart(current.totalCostFromStart - 1)
                .withStep(0);
        destroyedWrapper.destroyedObstacles.add(collision);
        return List.of(destroyedWrapper);
    }

    @NotNull
    private List<Point> obstaclesWithoutDestroyedAt(Wrapper current) {
        return pointsInMyWay.stream().filter(Predicate.not(current.destroyedObstacles::contains)).toList();
    }

    private List<G6Action> traceResult(Point start, Set<Point> pointsInMyWay, int stepSize, Wrapper current) {
        final List<PointAction> path = current.tracePath();
        final List<Point> points = path.stream().map(PointAction::location).toList();
        final var visualize = visualize(points, new ArrayList<>(pointsInMyWay), start);
        log.info(visualize);
        final List<G6Action> g6Actions = actionsFromPointActions(path, stepSize);
        log.info("g6Actions = " + g6Actions);
        return g6Actions;
    }

    private List<Wrapper> shouldHaveRotatedEarlier(Wrapper current, Move.AttachmentCollidingWithObstacleException e) {
        Point location = current.pointAction.location();
        Wrapper rotation1 = current
                .withPredecessor(current.predecessor)
                .withPointAction(new PointAction(location, new Rotate(CLOCKWISE), location))
                .withCompass(current.compass.rotate(CLOCKWISE))
                .withTotalCostFromStart(current.totalCostFromStart + 1)
                .withCostSum(current.costSum + 1);
        Wrapper rotation2 = current
                .withPredecessor(current.predecessor)
                .withPointAction(new PointAction(location, new Rotate(COUNTERCLOCKWISE), location))
                .withCompass(current.compass.rotate(COUNTERCLOCKWISE))
                .withTotalCostFromStart(current.totalCostFromStart + 1)
                .withCostSum(current.costSum + 1);
        return List.of(rotation1, rotation2);

    }
}
