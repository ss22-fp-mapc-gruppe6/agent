package g6Agent.services;

public enum Direction {
    n, s , e, w;

    public Point getVector() {
        return this.getDirection();
    }

    /**
     * direction to vector E -> (1,0)
     *
     * @return point - vector
     */
    public Point getDirection() {
        int x = 0;
        int y = 0;
        switch (this) {
            case w:
                x = -1;
                break;
            case e:
                x = 1;
                break;
            case n:
                y = -1;
                break;
            case s:
                y = 1;
                break;
            default:
                break;
        }
        return (new Point(x, y));
    }
}
