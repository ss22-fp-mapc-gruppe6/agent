package g6Agent.perceptionAndMemory.AgentMap;


import g6Agent.services.Point;

public class InternalMapEntry {
    private Point position;
    private int counter;

    public InternalMapEntry(Point position) {
        this.position = position;
        this.counter = 0;
    }
    public InternalMapEntry(Point position, int counter) {
        this.position = position;
        this.counter = counter;
    }
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getCounter() {
        return counter;
    }

    public void increaseCounter() {this.counter ++;}
}
