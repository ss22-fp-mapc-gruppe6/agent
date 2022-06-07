package g6Agent.ourPercepts;

public record Thing(int x, int y, Type type, String details) {
    Thing(int x, int y, String type, String details) {
        this(x, y, Type.valueOf(type), details);
    }

    public Thing(int x, int y, Type type, String details) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.details = details;
    }

    public enum Type {
        obstacle, entity, block, dispenser, marker
    }
}
