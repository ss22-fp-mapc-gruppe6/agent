package g6Agent.services;

public class Point extends java.awt.Point{

    public Point(int x, int y) {
        super(x, y);
    }

    /**
     * returns if the other point is adjacent to this Point
     * @param otherPoint the other Point
     * @return ist this Point adjacent to the other Point?
     */
    public boolean isAdjacentTo(Point otherPoint) {
        return otherPoint.equals(new Point(x, y+1)) || otherPoint.equals(new Point(x+1, y))
                || otherPoint.equals(new Point(x, y-1)) || otherPoint.equals(new Point(x-1, y));
    }


    /**
     * determines if this Point is adjacent to (0,0) Coordinate.
     * @return is the Point adjacent to th (0,0) Coordinate?
     */
    public boolean isAdjacent(){
        return ((x==0 && (y == 1 || y == -1)) || (y== 0 && (x == 1 || x == -1)));
    }

    /**
     * returns a new Point which is an inversion of this point
     * @return the inverted point
     */
    public  Point invert(){
        return new Point(-this.x, -this.y);
    }

    /**
     * Returns the Manhattan Distance from this Point to the given Point
     * @param point the given Point
     * @return the Manhattan Distance
     */
    public int manhattanDistanceTo(Point point){
        return (Math.abs(this.x - point.x) + Math.abs(this.y - point.y));
    }

    /**
     * Returns the Euclidean Distance from this Point to the given Point
     * @param point the given Point
     * @return the Euclidean Distance
     */
    public double euclideanDistanceTo(Point point){
        return Math.sqrt(Math.pow((this.x - point.x), 2) + Math.pow((this.y - point.y), 2));
    }


    public Point addAll(Point point){
        return new Point(this.x + point.x, this.y + point.y);
    }

   public void translate(Point vector) {
        this.add(vector);
    }

    private void add(Point vector) {
    }
}
