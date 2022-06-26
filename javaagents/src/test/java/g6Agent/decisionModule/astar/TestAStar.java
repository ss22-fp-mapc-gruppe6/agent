package g6Agent.decisionModule.astar;

import eis.iilang.Action;
import eis.iilang.IILElement;
import g6Agent.Tuple;
import g6Agent.actions.Clear;
import g6Agent.actions.G6Action;
import g6Agent.actions.Move;
import g6Agent.decisionModule.PointAction;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.services.Point;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAStar {
    public TestAStar() {
        IILElement.toProlog = true;
    }



    @Test
    public void test_unobstructed_directions_max() {
        /*  obstructions:
            - - - - b - - - -
            - - - - - - - - -
            b - - - x - - b -
            - - - - b - - - -
            - - - - - - - - -
            expected: n:1, e:2, s:no value, w:3
            but instead of int formatted as xy-point with negative north
            so n:(0,-1), e:(2,0), w:(-3,0)
         */
        final var obstructions = List.of(
                new Point(0, -2),
                new Point(3, 0),
                new Point(0, 1),
                new Point(-4, 0)
        );
        final var directions = AStar.getUnobstructedStepsMax(new HashSet<>(obstructions), 3);
        final List<Point> expected = List.of(
                new Point(0, -1),
                new Point(2, 0),
                new Point(-3, 0)
        );
        assertThat(directions, containsInAnyOrder(expected.toArray()));
        assertEquals(3, directions.size());
    }

    @Test
    public void test_unobstructed_directions_max_2() {
        /*  obstructions:
            - - - - b - - - -
            - - - - - - - - -
            - - - - - - - - -
            b - - - x - - - b
            - - - - - - - - -
            - - - - b - - - -
            - - - - - - - - -
            expected: n:2, e:3, s:1, w:3
            but instead of int formatted as xy-point with negative north
            so n:(0,-2), e:(3,0), s:(-1,0), w:(-3,0)
         */
        final var obstructions = List.of(
                new Point(0, -3),
                new Point(4, 0),
                new Point(0, 2),
                new Point(-4, 0)
        );
        final var directions = AStar.getUnobstructedStepsMax(new HashSet<>(obstructions), 3);
        final List<Point> expected = List.of(
                new Point(0, -2),
                new Point(3, 0),
                new Point(-3, 0),
                new Point(0, 1)
        );
        assertThat(directions, containsInAnyOrder(expected.toArray()));
        assertEquals(4, directions.size());
    }

    @Test
    public void test_unobstructed_directions_max_step_1() {
        /*  obstructions:
            - - - - b - - - -
            - - - - - - - - -
            - - - - - - - - -
            b - - - x - - - b
            - - - - - - - - -
            - - - - b - - - -
            - - - - - - - - -
            expected: n:1, e:1, s:1, w:1
            but instead of int formatted as xy-point with negative north
            so n:(0,-1), e:(1,0), s:(-1,0), w:(-1,0)
         */
        final var obstructions = List.of(
                new Point(0, -3),
                new Point(4, 0),
                new Point(0, 2),
                new Point(-4, 0)
        );
        final var directions = AStar.getUnobstructedStepsMax(new HashSet<>(obstructions), 1);
        final List<Point> expected = List.of(
                new Point(0, -1),
                new Point(1, 0),
                new Point(-1, 0),
                new Point(0, 1)
        );
        assertThat(directions, containsInAnyOrder(expected.toArray()));
        assertEquals(4, directions.size());
    }

    @Test
    public void test_unobstructed_directions_all() {
        /*  obstructions:
            - - - - b - - - -
            - - - - - - - - -
            b - - - x - - b -
            - - - - b - - - -
            - - - - - - - - -
            expected: n:1; e:1,2; s:no value; w:1,2,3
            but instead of int formatted as xy-point with negative north
            so n:(0,-1); e:(1,0),(2,0); w:(-1,0),(-2,0),(-3,0)
         */
        final var obstructions = Set.of(
                new Point(0, -2),
                new Point(3, 0),
                new Point(0, 1),
                new Point(-4, 0)
        );
        final var directions = AStar.getUnobstructedSteps(obstructions, 3, new Point(0, 0));
        final List<Point> expected = List.of(
                new Point(0, -1),
                new Point(1, 0),
                new Point(2, 0),
                new Point(-1, 0),
                new Point(-2, 0),
                new Point(-3, 0)
        );
        final List<Point> points = directions.stream()
                .filter(pointClassTuple -> Move.class.equals(pointClassTuple.b()))
                .map(Tuple::a).toList();
        assertThat(points, containsInAnyOrder(expected.toArray()));
        assertEquals(6, points.size());

    }

    @Test
    public void test_0_0() {
        Point target = new Point(0, 0);
        final var shortestPath = AStar.findShortestPath(target, List.of(), 5);
        assertEquals(0, shortestPath.size());
        assertEquals(List.of(), shortestPath);
    }

    @Test
    public void test_points_to_directions() {
        Point target = new Point(2, 3);
        final List<Point> obstacles = List.of(
//                new Point(8, 3),
//                new Point(8, 4),
//                new Point(8, 5),
//                new Point(8, 6),
//                new Point(8, 8),
//                new Point(8, 7),
//                new Point(8, 9)
        );
        final var shortestPath = AStar.findShortestPath(target, obstacles, 3);
        System.out.println("shortestPath = " + shortestPath);
//        final List<Point> points = shortestPath.stream().map(PointAction::from).toList();
//        final var visualize = visualize(points, obstacles);
//        assertEquals(5, points.size());

    }

    @Test
    public void test_9_9() {
        Point start = new Point(5, 5);
        Point target = new Point(12, 9);
        List<Point> obstacles = new ArrayList<>();
        List<Point> line = new ArrayList<>();
        for (int i = -10; i < 30; i++) line.add(new Point(8, 0 + i));
        obstacles.addAll(line);
        List<Point> cage = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (x != 0 || y != 0) {
                    cage.add(target.add(x, y));
                }
            }
        }
//        obstacles.addAll(cage);
        final var shortestPath = AStar.findShortestPath(start, target, obstacles, 3);
        System.out.println("shortestPath = " + shortestPath);
        //TODO fix
//        final List<Point> points = shortestPath.stream().map(PointAction::from).toList();
//        System.out.println("Direction.directionsFrom(points) = " + Point.fromPointToPoint(points));
//        final var visualize = visualize(points, obstacles, start);
//        System.out.println(visualize);
        assertEquals(5, shortestPath.size());
    }

    @Test
    public void test_3_3() {
        final Point start = new Point(1, 1);
        Point target = new Point(2, 3);
        List<Point> obstacles = new ArrayList<>();
        List<Point> line = new ArrayList<>();
        for (int i = 0; i < 5; i++) line.add(new Point(8, 3 + i));
        obstacles.addAll(line);
        List<Point> cage = new ArrayList<>();
        for (int x = -1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                if (x != 0 && y != 0) {
                    cage.add(target.add(x, y));
                }
            }
        }
        obstacles.addAll(cage);
        final var shortestPath = AStar.findShortestPath(start, target, obstacles, 3);
        System.out.println("shortestPath = " + shortestPath);
        //TODO fix
//        List<Point> points = shortestPath.stream().map(PointAction::from).toList();
//        points = new ArrayList<>(points);
//        points.add(0, start);
//        final var visualize = visualize(points, obstacles, start);
//        System.out.println(visualize);

//        assertEquals(4, shortestPath.size());
    }

    @Test
    public void getNeighbours() {
        final var neighbours = AStar.getNeighbours(new Point(0, 0));
        assertEquals(Set.of(
                new Point(0, 1),
                new Point(0, -1),
                new Point(1, 0),
                new Point(-1, 0)
        ), neighbours);
    }

    @Test
    public void getNeighbours2() {
        final var neighbours = AStar.getNeighbours(new Point(3, 4));
        assertEquals(Set.of(
                new Point(3, 5),
                new Point(4, 4),
                new Point(3, 3),
                new Point(2, 4)
        ), neighbours);
    }

    @Test
    public void genericsTest() {

        final List<Tuple<Point, Class<? extends G6Action>>> unobstructed = new ArrayList<>();

        unobstructed.add(new Tuple<>(new Point(3, 3), Move.class));
        unobstructed.add(new Tuple<>(new Point(3, 3), Move.class));
        unobstructed.add(new Tuple<>(new Point(3, 3), Move.class));
        unobstructed.add(new Tuple<>(new Point(0, 0), Clear.class));
        unobstructed.add(new Tuple<>(new Point(3, 3), Move.class));
        for (Tuple<Point, Class<? extends G6Action>> pointClassTuple : unobstructed) {
            final Class<? extends G6Action> b = pointClassTuple.b();
            final Map<? extends Class<? extends Action>, Runnable> classRunnableMap = Map.of(
                    Move.class, () -> {
                        System.out.println("it's a move");
                    },
                    Clear.class, () -> {
                        System.out.println("it's a clear");
                    }
            );
            if (b.equals(Move.class)) {

            }
            final Runnable runnable = classRunnableMap.get(b);
            runnable.run();
        }
    }

    @Test
    public void streamTest() {

        final PointAction p1 = new PointAction(new Point(1, 2), Move.class, new Point(2, 3));
        final PointAction p2 = new PointAction(new Point(2, 3), Move.class, new Point(4, 3));
        final PointAction p3 = new PointAction(new Point(4, 3), Move.class, new Point(5, 5));


        System.out.println(Stream.of(p1, p2,p3)
                .sequential()
                .flatMap(pa -> Stream.of(pa.from(), pa.to()))
                .distinct()
                .toList());
    }
}
