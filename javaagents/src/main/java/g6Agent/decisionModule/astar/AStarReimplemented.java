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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static g6Agent.services.Direction.*;
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
    final Map<PointAction, AStar.Wrapper> wrappers = new HashMap<>();
    final Set<ComparableTuple<PointAction, Direction>> visited = new TreeSet<>();
    volatile Queue<AStar.Wrapper> queue = new PriorityBlockingQueue<>(20, AStar.Wrapper::compareTo);
    PointAction startPointAction;
    AStar.Wrapper startWrapper;

    public static G6Action astarNextStep(Point target, PerceptionAndMemory perceptionAndMemory) {
        return astarShortestPath(target, perceptionAndMemory).stream().findFirst().orElseGet(Skip::new);
    }

    public static List<G6Action> astarShortestPath(Point target, PerceptionAndMemory perceptionAndMemory) {
        try {
            FileHandler fh = new FileHandler("./astar.log");
            log.addHandler(fh);
//            SimpleFormatter formatter = new SimpleFormatter();
//            fh.setFormatter(formatter);
//            log.setUseParentHandlers(false);
            log.info("My first log");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final List<Point> directlyAttachedBlocks = perceptionAndMemory.getDirectlyAttachedBlocks().stream().map(Block::getCoordinates).toList();
        final List<Integer> movementSpeed = perceptionAndMemory.getCurrentRole().getMovementSpeed();
        final Integer stepSize = movementSpeed.get(directlyAttachedBlocks.size());
        final List<Point> blockingBlocks = perceptionAndMemory.getBlocks().stream().map(Block::getCoordinates).filter(Predicate.not(directlyAttachedBlocks::contains)).toList();
        TreeSet<Point> pointsInMyWay = Stream.concat(perceptionAndMemory.getObstacles().stream(), blockingBlocks.stream()).collect(Collectors.toCollection(TreeSet::new));
        AStarReimplemented aStar = new AStarReimplemented(new Point(0, 0), target, stepSize, directlyAttachedBlocks, pointsInMyWay);
        log.info("target = " + target);
        return aStar.findShortestPath();
    }

    List<G6Action> findShortestPath() {
        startPointAction = new PointAction(start, new Move(), start);
        startWrapper = new AStar.Wrapper(startPointAction, null, 0.0, 0.0, target.euclideanDistanceTo(start), Set.of(), 0, attachments, Direction.NORTH, new Stack<>());
        wrappers.put(startPointAction, startWrapper);
        queue.add(startWrapper);

        ExecutorService executorService = Executors.newCachedThreadPool();
        while (!queue.isEmpty()) {
            final var current = queue.poll();

            if (current.pointAction.action() instanceof Move && current.pointAction.target().equals(target)) {
                log.info("############target reached################");
                return traceResult(start, pointsInMyWay, stepSize, current);
            }

            CompletableFuture<List<AStar.Wrapper>> listCompletableFuture = CompletableFuture.supplyAsync(
                    () -> getNextSteps(current),
                    executorService
            );
            if (queue.isEmpty()){
                try {
                    List<AStar.Wrapper> next = listCompletableFuture.get();
                    queue.addAll(next);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }else{
                listCompletableFuture.thenAccept(queue::addAll);
            }

        }

        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return null;
    }

    List<AStar.Wrapper> getNextSteps(AStar.Wrapper current) {

        log.info("current = " + current);
        ComparableTuple<PointAction, Direction> pointAndDirection = new ComparableTuple<>(current.pointAction, current.compass);
        if (visited.contains(pointAndDirection)) {
            return List.of();
        }
        visited.add(pointAndDirection);

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
                    shouldHaveRotatedEarlier(current, move);
                    return List.of();
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
//                        queue.add(current.postponed.pop());
                } catch (Rotate.AttachmentCollidingWithObstacleException e) {
                    //TODO go deeper?
//                        shouldHaveRotatedEarlier(current, rotate);
                    return List.of();
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + action);
        }

        final var adjacentActions = AStar.getNextActions(
                obstaclesWithoutDestroyedAt(current),
                currentLocation
        );

        List<AStar.Wrapper> nextSteps = new ArrayList<>();
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
                    cost = 200000;
                    destroyedObstacles.add(nextTarget);
                }
                default -> throw new IllegalStateException("Unexpected value: " + kv);
            }
            final PointAction pointAction = new PointAction(currentLocation, nextAction, nextTarget);
            final double totalCost = current.totalCostFromStart + cost;
            final double minimumRemainingCost = nextTarget.euclideanDistanceTo(target);
// no wrappers.get(pointAction)
            AStar.Wrapper nextWrapped = null;
            nextWrapped = current
                    .withPointAction(pointAction)
                    .withPredecessor(current)
                    .withCostSum(totalCost + minimumRemainingCost)
                    .withTotalCostFromStart(totalCost)
                    .withMinimalRemainingCost(minimumRemainingCost)
                    .withDestroyedObstacles(destroyedObstacles)
                    .withStep(step);
            wrappers.put(pointAction, nextWrapped);
            nextSteps.add(nextWrapped);
        }
        return nextSteps;
    }

    @NotNull
    private List<Point> obstaclesWithoutDestroyedAt(AStar.Wrapper current) {
        return pointsInMyWay.stream().filter(Predicate.not(current.destroyedObstacles::contains)).toList();
    }

    private List<G6Action> traceResult(Point start, Set<Point> pointsInMyWay, int stepSize, AStar.Wrapper current) {
        final List<PointAction> path = current.tracePath();
        final List<Point> points = path.stream().map(PointAction::location).toList();
        final var visualize = AStar.visualize(points, new ArrayList<>(pointsInMyWay), start);
        System.out.println(visualize);
        final List<G6Action> g6Actions = AStar.actionsFromPointActions(path, stepSize);
        System.out.println("g6Actions = " + g6Actions);
        return g6Actions;
    }

    private ComparableTuple<AStar.Wrapper, AStar.Wrapper> shouldHaveRotatedEarlier(AStar.Wrapper current, G6Action tryingTo) {

        Rotate cw = new Rotate(CLOCKWISE);
        Rotate ccw = new Rotate(COUNTERCLOCKWISE);

        Point location = current.pointAction.location();
        PointAction key1 = new PointAction(location, cw, location);
        AStar.Wrapper rotation1 = current
                .withPredecessor(current.predecessor)
                .withPointAction(key1)
                .withCompass(current.compass.rotate(CLOCKWISE))
                .withTotalCostFromStart(current.totalCostFromStart + 1)
                .withCostSum(current.totalCostFromStart + 1 + current.minimalRemainingCost);
//        rotation1.postponed.push(
//                current
//                .withPredecessor(rotation1)
//                .withCompass(current.compass.rotate(CLOCKWISE))
//                .withCostSum(current.costSum + 2)
//                .withTotalCostFromStart(current.totalCostFromStart + 2)
//        );
        wrappers.put(key1, rotation1);
        PointAction key2 = new PointAction(location, ccw, location);
        AStar.Wrapper rotation2 = current
                .withPredecessor(current.predecessor)
                .withPointAction(key2)
                .withCompass(current.compass.rotate(COUNTERCLOCKWISE))
                .withTotalCostFromStart(current.totalCostFromStart + 1)
                .withCostSum(current.totalCostFromStart + 1 + current.minimalRemainingCost);
//        rotation2.postponed.push(current
//                .withPredecessor(rotation2)
//                .withCompass(current.compass.rotate(COUNTERCLOCKWISE))
//                .withCostSum(current.costSum + 2)
//                .withTotalCostFromStart(current.totalCostFromStart + 2)
//                .withCompass(current.compass.rotate(COUNTERCLOCKWISE))
//        );
        wrappers.put(key2, rotation2);
        queue.add(rotation1);
        queue.add(rotation2);
//        visited.remove(new ComparableTuple<>(current.pointAction, current.compass.rotate(CLOCKWISE)));
//        visited.remove(new ComparableTuple<>(current.pointAction, current.compass.rotate(COUNTERCLOCKWISE)));
        return new ComparableTuple<>(rotation1, rotation2);

    }

    Collection<Point> relativeToAbsolute(Collection<Point> points, AStar.Wrapper current) {
        return points.stream().map(p -> p.add(current.pointAction.location())).toList();
    }
}
