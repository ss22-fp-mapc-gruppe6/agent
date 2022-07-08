package g6Agent.communicationModule.submodules;

import g6Agent.services.Point;

/**
 * A Filter to decide which agents recieve pings
 * Also to recalculate the relative position of pings
 */
public interface PingFilter {
    /**
     * Determines if this agent will receive the ping in its actual state
     * @param point the pings position as given by the sender
     * @param sender the sender
     * @return will the agent recieve the ping?
     */
    boolean pingAccepted(Point point, String sender);

    /**
     * recalculates the point in relation to the relative position of the sender
     * returns null if the relative position is unknown.
     * @param point the pings position as given by the sender
     * @param sender the sender
     * @return the recalculated point
     */
    Point recalculatePingPosition(Point point, String sender);
}
