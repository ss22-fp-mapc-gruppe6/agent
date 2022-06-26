package g6Agent.goals;

import g6Agent.communicationModule.submodules.PingListener;
import g6Agent.services.Point;

//enables a goal to send and receive pings
public interface PingReceiver {
    public void addPingListener(PingListener pingListener);
    public void recievePing(Point ping);
}
