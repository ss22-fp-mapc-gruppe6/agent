package g6Agent.ourPercepts;

import g6Agent.services.Point;

public record Attached(Point point) {
    public Attached(int x, int y) {
        this(new Point(x, y));
    }
    public Attached(Point point) {
        this.point = point;
    }

}
