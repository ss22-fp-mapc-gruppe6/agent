package g6Agent.ourPercepts;

import g6Agent.services.Point;

public record GoalZone(Point point) {

    public GoalZone(int x, int y) {
        this(new Point(x, y));
    }

    public GoalZone(Point point) {
        this.point = point;
    }
}
