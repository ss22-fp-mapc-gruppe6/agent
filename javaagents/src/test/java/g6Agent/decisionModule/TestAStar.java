package g6Agent.decisionModule;

import g6Agent.services.Point;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAStar {
    @Test
    public void test_unobstructed_directions() {
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
        final var directions = AStar.getMaxUnobstructedSteps(obstructions, 3);
        final List<Point> expected = List.of(
                new Point(0, -1),
                new Point(2, 0),
                new Point(-3, 0)
        );
        expected.forEach(e -> assertTrue(directions.contains(e)));
        assertEquals( 3, directions.size());

    }

    public static String visualize(List<Point> path, List<Point> obstacles) {
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

        Point start = path.get(0);
        Point target = path.get(path.size() - 1);


        String fielGap = " ";
        StringBuffer s = new StringBuffer();
        // x axis legend
        s.append(" ").append(" ").append(" ");
        for (int x = 0; x <= maxX; x++) {
            s.append(String.format("%5s", x));
        }
        s.append("\n");

        for (int y = minY; y <= maxY; y++) {
            //y axis legend
            s.append(String.format("%3s", y));

            for (int x = minX; x <= maxX; x++) {
                String field = " ";
                if (start.getX() == x && start.getY() == y)
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

    @Test
    public void test_unobstructed_directions() {
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
        final var directions = AStar.getMaxUnobstructedSteps(obstructions, 3);
        final List<Point> expected = List.of(
                new Point(0, -1),
                new Point(2, 0),
                new Point(-3, 0)
        );
        expected.forEach(e -> assertTrue(directions.contains(e)));
        assertEquals( 3, directions.size());

    }

    @Test
    public void test_0_0() {
        Point target = new Point(0, 0);
        final var shortestPath = AStar.findShortestPath(target, List.of(), 1);
        assertEquals(0, shortestPath.size());
        assertEquals(List.of(), shortestPath);
    }

    @Test
    public void test_points_to_directions() {
        Point target = new Point(12, 9);
        final var obstacles = List.of(
                new Point(8, 3),
                new Point(8, 4),
                new Point(8, 5),
                new Point(8, 6),
                new Point(8, 8),
                new Point(8, 7),
                new Point(8, 9)
        );
        final var shortestPath = AStar.findShortestPath(target, obstacles, 1);
        System.out.println("shortestPath = " + shortestPath);
        final var visualize = visualize(shortestPath);
        System.out.println(visualize);

        final var directions = AStar.directionsFrom(shortestPath);
        System.out.println("directions = " + directions);
    }

    //    @Test
    public void test_9_9() {
        Point start = new Point(4, 5);
        Point target = new Point(12, 7);
        final var obstacles = List.of(
                new Point(8, 3),
                new Point(8, 4),
                new Point(8, 5),
                new Point(8, 6),
                new Point(8, 8),
                new Point(8, 7),
                new Point(8, 9)
        );
        final var shortestPath = AStar.findShortestPath(start, target, obstacles, 1);
        System.out.println("shortestPath = " + shortestPath);
        final var visualize = visualize(shortestPath, obstacles);
        System.out.println(visualize);

        assertEquals(4, shortestPath.size());
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
}
