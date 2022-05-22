package g6Agent.agents.decisionModule;

import g6Agent.services.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TestAStar {

    @Test
    public void test_0_0() {
        Point target = new Point(0, 0);
        List<Point> route = getPath(target);
        assert route.isEmpty();
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
        String s = visualize(path);
        System.out.println("s: " + System.lineSeparator() + s);
    }

    private String visualize(List<Point> path) {
        Optional<Point> maxXPoint = path.stream().max(Comparator.comparing(Point::getX));
        Optional<Point> maxYPoint = path.stream().max(Comparator.comparing(Point::getY));
        if (maxXPoint.isEmpty() || maxYPoint.isEmpty())
            throw new IllegalArgumentException("x or y dimension is empty?");
        if (path.isEmpty())
            throw new IllegalArgumentException("list is empty");
        double maxX = maxXPoint.get().getX();
        double maxY = maxYPoint.get().getY();

        Point start = path.get(0);
        Point target = path.get(path.size() - 1);


        String fielGap = " ";
        StringBuffer s = new StringBuffer();
        // x axis legend
        s.append(" ").append(" ").append(" ");
        for (int x = 0; x <= maxX; x++) {
            s.append(" ").append(" ").append(x).append(" ");
        }
        s.append(System.lineSeparator());

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
            s.append(System.lineSeparator());
        }

        return s.toString();
    }

    private List<Point> getPath(Point target) {
        return new ArrayList<>();
    }


}
