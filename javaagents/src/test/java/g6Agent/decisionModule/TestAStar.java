package g6Agent.decisionModule;

import g6Agent.services.Point;
import org.junit.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAStar {

    public static String visualize(List<Point> path) {
        Optional<Point> maxXPoint = path.stream().max(Comparator.comparing(Point::getX));
        Optional<Point> maxYPoint = path.stream().max(Comparator.comparing(Point::getY));
        if (path.isEmpty())
            throw new IllegalArgumentException("list is empty");
        if (maxXPoint.isEmpty() || maxYPoint.isEmpty())
            throw new IllegalArgumentException("x or y dimension is empty?");
        double maxX = maxXPoint.get().getX();
        double maxY = maxYPoint.get().getY();

        Point start = path.get(0);
        Point target = path.get(path.size() - 1);


        String fielGap = " ";
        StringBuffer s = new StringBuffer();
        // x axis legend
        s.append(" ").append(" ");
        for (int x = 0; x <= maxX; x++) {
            s.append(" ").append(" ").append(" ").append(x);
        }
        s.append("\n");

        for (int y = 0; y <= maxY; y++) {
            //y axis legend
            s.append(" ").append(y).append(" ");

            for (int x = 0; x <= maxX; x++) {
                s.append(fielGap);
                String field = " ";
                if (start.getX() == x && start.getY() == y)
                    field = "s";
                else if (target.getX() == x && target.getY() == y)
                    field = "t";
                else {
                    for (int i = 1; i < path.size(); i++) {
                        Point p = path.get(i);
                        if (p.getX() == x && p.getY() == y) {
                            field = String.valueOf(i);
                            break;
                        }
                    }
                }
                String fieldFormat = String.format("[%s]", field);
                s.append(fieldFormat);
            }
            s.append("\n");
        }

        return s.toString();
    }

    @Test
    public void test_0_0() {
        Point target = new Point(0, 0);
        final var shortestPath = AStar.findShortestPath(target, target);
        assertEquals(0, shortestPath.size());
        assertEquals(List.of(target), shortestPath);
    }

    @Test
    public void test_9_9() {
        Point start = new Point(0, 0);
        Point target = new Point(2, 2);
        final var shortestPath = AStar.findShortestPath(start, target);
        assertEquals(2, shortestPath.size());
        assertEquals(List.of(new Point(1, 0), new Point(1, 1)), shortestPath);
    }

    @Test
    public void test_ascii() {
        List<Point> path = List.of(
                new Point(1, 1),
                new Point(2, 1),
                new Point(2, 2),
                new Point(2, 3),
                new Point(9, 9),
                new Point(3, 3));
        String expected = """
                     0   1   2   3   4   5   6   7   8   9
                 0  [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 1  [ ] [s] [1] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 2  [ ] [ ] [2] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 3  [ ] [ ] [3] [t] [ ] [ ] [ ] [ ] [ ] [ ]
                 4  [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 5  [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 6  [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 7  [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 8  [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ]
                 9  [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [4]
                """;
        assertEquals(expected, visualize(path));
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
