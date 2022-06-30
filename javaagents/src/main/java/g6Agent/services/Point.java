package g6Agent.services;


import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Point extends java.awt.Point {


    public Point(java.awt.Point p) {
        this(p.x, p.y);
    }


    /**
     * Dangerous! Use {@link #add(int, int)} or {@link #add(Point)} instead!
     */
    @Deprecated
    @Override
    public void translate(int dx, int dy)  {
        throw new IllegalStateException("please don't use this method! Use `add(int, int)` instead");
    }

    public Point add(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    public Point(int x, int y) {
        super(x, y);
    }

    /**
     * returns if the other point is adjacent to this Point
     *
     * @param otherPoint the other Point
     * @return ist this Point adjacent to the other Point?
     */
    public boolean isAdjacentTo(Point otherPoint) {
        return otherPoint.equals(new Point(x, y + 1)) || otherPoint.equals(new Point(x + 1, y))
                || otherPoint.equals(new Point(x, y - 1)) || otherPoint.equals(new Point(x - 1, y));
    }


    /**
     * determines if this Point is adjacent to (0,0) Coordinate.
     *
     * @return is the Point adjacent to th (0,0) Coordinate?
     */
    public boolean isAdjacent() {
        return ((x == 0 && (y == 1 || y == -1)) || (y == 0 && (x == 1 || x == -1)));
    }

    /**
     * returns a new Point which is an inversion of this point
     *
     * @return the inverted point
     */
    public Point invert() {
        return new Point(-this.x, -this.y);
    }

    /**
     * Returns the Manhattan Distance from this Point to the given Point
     *
     * @param point the given Point
     * @return the Manhattan Distance
     */
    public int manhattanDistanceTo(Point point) {
        return (Math.abs(this.x - point.x) + Math.abs(this.y - point.y));
    }

    /**
     * Returns the Euclidean Distance from this Point to the given Point
     *
     * @param point the given Point
     * @return the Euclidean Distance
     */
    public double euclideanDistanceTo(Point point) {
        return Math.sqrt(Math.pow((this.x - point.x), 2) + Math.pow((this.y - point.y), 2));
    }

    /**
     * @param multiplier the number the point coordinates will be multiplied with
     * @return a new Point, where the x and y coordinates are multiplied with the multiplier
     */
    public Point multiply(int multiplier) {
        return new Point(
                x * multiplier,
                y * multiplier
        );
    }


    /**
     * Adds the x and y Coordinates of the two points
     *
     * @param pointToAdd the point to add
     * @return new Point((x1+x2), (y1+y2))
     */
    public Point add(Point pointToAdd) {
        return new Point((this.x + pointToAdd.x), (this.y + pointToAdd.y));
    }

    public Point addAll(Point point) {
        return new Point(this.x + point.x, this.y + point.y);
    }

    /**
     * returns a new Point that is a rotatet represantation of this point over the (0,0) Coordinate
     *
     * @param rotation the rotation direction
     */
    public Point rotate(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE -> {
                return new Point(-(int) this.getY(), (int) this.getX());
            }
            case COUNTERCLOCKWISE -> {
                return new Point((int) this.getY(), -(int) this.getX());
            }
        }
        //can not happen
        return null;
    }


    public static List<Point> fromPointToPoint(List<Point> points) {
        Point previous = new Point(0, 0);
        List<Point> delta = new LinkedList<>();
        for (Point current : points) {
            int x = current.x - previous.x;
            int y = current.y - previous.y;
            delta.add(new Point(x,y));
            previous = current;
        }
        return delta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
    public static Comparator<Point> byDistanceToOrigin() {
        return Comparator.comparingInt(point -> point.manhattanDistanceTo(new Point(0,0)));
    }

}
