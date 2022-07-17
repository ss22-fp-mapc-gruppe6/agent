package g6Agent.perceptionAndMemory.Enties;

import g6Agent.services.Point;

public record AgentNameAndPosition(String name, Point position) {
    public String getName() {
        return name;}
    public Point getPosition() {
        return position;}
    }
