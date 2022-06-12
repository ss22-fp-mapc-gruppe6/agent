package g6Agent.perceptionAndMemory;


import g6Agent.services.Point;

 class InternalMapEntry {
    private Point position;
    private int counter;

    InternalMapEntry(Point position) {
        this.position = position;
        this.counter = 0;
    }
    InternalMapEntry(Point position, int counter) {
        this.position = position;
        this.counter = counter;
    }
    Point getPosition() {
        return position;
    }

    void setPosition(Point position) {
        this.position = position;
    }

    int getCounter() {
        return counter;
    }

    void increaseCounter() {this.counter ++;}
}
