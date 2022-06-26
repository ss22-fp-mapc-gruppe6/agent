package g6Agent.decisionModule.submodules;

import g6Agent.services.Point;

/**
 * A Filter to decide which agents recieve pings
 */
public interface PingFilter {
    public boolean pingAccepted(Point point, String sender);
}
