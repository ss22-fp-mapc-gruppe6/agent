package g6Agent.ourPercepts;

import g6Agent.services.Point;

public record RoleZone(Point point) {
    public RoleZone(int x, int y) {
        this(new Point(x, y));
    }

    public RoleZone(Point point) {
        this.point = point;
    }
}
