package g6Agent.goals.interfaces;

import g6Agent.communicationModule.submodules.PingListener;
import g6Agent.services.Point;

//enables a goal to send and receive pings
public interface PingReceiver {
    void addPingListener(PingListener pingListener);
    void recievePing(Point ping);
}
