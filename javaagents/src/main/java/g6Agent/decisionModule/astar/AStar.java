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

import static g6Agent.services.Rotation.CLOCKWISE;
import static g6Agent.services.Rotation.COUNTERCLOCKWISE;

@RequiredArgsConstructor
@Log
public class AStar {
    final Point start;
    final Point target;
    final int stepSize;
    final List<Point> attachments;
    final Set<Point> pointsInMyWay;
    final Set<ComparableTuple<PointAction, Direction>> visited = new TreeSet<>();
    private final int clearCost = 1;
    private final int firstStepCost = 2;
    private final int rotationCost = 1;
    volatile Queue<Wrapper> queue = new PriorityQueue<>(Wrapper::compareTo);
    private final PerceptionAndMemory pem;

    public static Optional<G6Action> astarNextStepWithAgents(Point target, PerceptionAndMemory perceptionAndMemory) {
        return astarShortestPathWithAgents(target, perceptionAndMemory).stream().findFirst();
    }

    public static List<G6Action> astarShortestPathWithAgents(Point target, PerceptionAndMemory perceptionAndMemory) {
        final List<Point> directlyAttachedBlocks = perceptionAndMemory.getDirectlyAttachedBlocks().stream().map(Block::getCoordinates).toList();
        final List<Integer> movementSpeed = perceptionAndMemory.getCurrentRole().getMovementSpeed();
        final Integer stepSize = movementSpeed.get(directlyAttachedBlocks.size());
        final Set<Point> pointsInMyWay = new TreeSet<>();
        pointsInMyWay.addAll(perceptionAndMemory.getBlocks().stream().map(Block::getCoordinates).filter(Predicate.not(directlyAttachedBlocks::contains)).toList());
        pointsInMyWay.addAll(perceptionAndMemory.getFriendlyAgents());
        pointsInMyWay.addAll(perceptionAndMemory.getEnemyAgents());
        pointsInMyWay.addAll(perceptionAndMemory.getObstacles());
        AStar aStar = new AStar(new Point(0, 0), target, stepSize, directlyAttachedBlocks, pointsInMyWay, perceptionAndMemory);
        List<G6Action> shortestPath = aStar.findShortestPath();
        return shortestPath;
    }

    List<G6Action> findShortestPath() {
        PointAction startPointAction = new PointAction(start, new Move(), start);
        Wrapper startWrapper = new Wrapper(startPointAction, null, 0.0, 0.0, target.euclideanDistanceTo(start), Set.of(), 0, attachments, Direction.NORTH, new Stack<>());
        queue.add(startWrapper);
        final var start = System.currentTimeMillis();

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            if (current.pointAction.location().equals(target)) {
                log.info("found path in " + (System.currentTimeMillis() - start) + "ms");
                List<G6Action> g6Actions = traceResult(this.start, pointsInMyWay, stepSize, current.predecessor == null ? current : current.predecessor);
                return g6Actions;
            }
            List<Wrapper> nextSteps = getNextSteps(current);
            queue.addAll(nextSteps);
            if (System.currentTimeMillis() - start > 500) {
                log.severe(String.format("Agent %s ran out of time!", pem.getName()));
                return List.of();
            }
        }

        new RuntimeException("No path to " + target + " found.").printStackTrace();
        return List.of();
    }

    private List<G6Action> traceResult(Point start, Set<Point> pointsInMyWay, int stepSize, Wrapper current) {
        final List<PointAction> path = current.tracePath();
        final List<Point> points = path.stream().map(PointAction::location).toList();
//        final var visualize = visualize(points, new ArrayList<>(pointsInMyWay), start);
        //log.info(visualize);
        final List<G6Action> g6Actions = actionsFromPointActions(path, stepSize);
        //log.info("g6Actions = " + g6Actions);
        return g6Actions;
    }

    List<G6Action> actionsFromPointActions(List<PointAction> path, int stepSize) {
        final List<G6Action> actions = new ArrayList<>();
        final LinkedList<PointAction> queue = new LinkedList<>();
        int step = 0;
        for (final PointAction pa : path) {
            final G6Action action = pa.action();
            if (action instanceof Move) {
                step++;
                queue.add(pa);
                if (step >= stepSize) {
                    step = 0;
                    actions.add(bundle(queue));
                    queue.clear();
                }
            } else if (action instanceof Clear clear) {
                step = 0;
                //add queued partial move before clear
                if (!queue.isEmpty()) {
                    actions.add(bundle(queue));
                    queue.clear();
                }
                actions.add(clear);
            } else if (action instanceof Rotate rotate) {
                step = 0;
                //add queued partial move before clear
                if (!queue.isEmpty()) {
                    actions.add(bundle(queue));
                    queue.clear();
                }
                actions.add(rotate);
            } else {
                throw new IllegalStateException("Unexpected value: " + action);
            }
        }
        //add partial move
        if (!queue.isEmpty()) actions.add(bundle(queue));
        return actions;
    }

    Move bundle(LinkedList<PointAction> pointActions) {
        return new Move(pointActions.stream().sequential()
                .map(PointAction::action)
                .map(g6Action -> ((Move) g6Action))
                .flatMap(move -> move.directions.stream().sequential())
                .toArray(Direction[]::new)
        );
    }

    List<Wrapper> getNextSteps(Wrapper current) {
        ComparableTuple<PointAction, Direction> pointAndDirection = new ComparableTuple<>(current.pointAction, current.compass);
        if (visited.contains(pointAndDirection)) {
            return List.of();
        }

        G6Action action = current.pointAction.action();
        final Point currentLocation;
        if (action instanceof Move move) {
            try {
                move.predictSuccess(
                        current.attachments
                                .stream().map(p -> p.add(current.pointAction.location())).toList(),
                        obstaclesWithoutDestroyedAt(current)
                                .stream().map(p -> p.add(current.pointAction.location())).toList()
                );
            } catch (Move.AttachmentCollidingWithObstacleException e) {
                return shouldHaveRotatedEarlier(current);
            }
            currentLocation = current.pointAction.target();
        } else if (action instanceof Clear) {
            currentLocation = current.pointAction.location();
            current.destroyedObstacles.add(current.pointAction.target());
        } else if (action instanceof Rotate rotate) {
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
        } else {
            throw new IllegalStateException("Unexpected value: " + action);
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
            if (nextAction instanceof Move) {
                if (current.pointAction.action() instanceof Move && current.pointAction.location().equals(nextTarget)) {
                    continue;
                }
                step++;
                if (step == 1) cost = firstStepCost;
                else if (step <= stepSize) cost = 0;
                else {
                    step = 1;
                    cost = firstStepCost;
                }
            } else if (nextAction instanceof Clear) {
                step = 0;
                cost = clearCost;
                destroyedObstacles.add(nextTarget);
            } else {
                throw new IllegalStateException("Unexpected value: " + kv);
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

    @NotNull
    private List<Point> obstaclesWithoutDestroyedAt(Wrapper current) {
        return pointsInMyWay.stream().filter(Predicate.not(current.destroyedObstacles::contains)).toList();
    }

    private List<Wrapper> shouldHaveRotatedEarlier(Wrapper current) {
        Point location = current.pointAction.location();
        Wrapper rotation1 = current
                .withPredecessor(current.predecessor)
                .withPointAction(new PointAction(location, new Rotate(CLOCKWISE), location))
                .withCompass(current.compass.rotate(CLOCKWISE))
                .withTotalCostFromStart(current.totalCostFromStart + rotationCost)
                .withCostSum(current.costSum + rotationCost);
        Wrapper rotation2 = current
                .withPredecessor(current.predecessor)
                .withPointAction(new PointAction(location, new Rotate(COUNTERCLOCKWISE), location))
                .withCompass(current.compass.rotate(COUNTERCLOCKWISE))
                .withTotalCostFromStart(current.totalCostFromStart + rotationCost)
                .withCostSum(current.costSum + rotationCost);
        return List.of(rotation1, rotation2);

    }

    private List<Wrapper> shouldHaveClearedEarlier(Wrapper current, Rotate.AttachmentCollidingWithObstacleException e) {
        Point collision = e.getCollision().add(current.pointAction.location().invert());
        HashSet<Point> destroyed = new HashSet<>(current.destroyedObstacles);
        destroyed.add(collision);

        Wrapper destroyedWrapper = current
                .withPointAction(new PointAction(current.getPointAction().location(), new Clear(collision), collision))
                .withPredecessor(current)
                .withCostSum(current.costSum + clearCost)
                .withTotalCostFromStart(current.totalCostFromStart + clearCost)
                .withDestroyedObstacles(destroyed)
                .withStep(0);
        return List.of(destroyedWrapper);
    }

    Map<Point, G6Action> getNextActions(List<Point> obstacles, Point origin) {
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

    String visualize(List<Point> path, List<Point> obstacles, Point start) {
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
}
