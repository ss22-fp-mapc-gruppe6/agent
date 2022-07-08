package g6Agent.communicationModule.submodules;

import eis.iilang.Percept;
import g6Agent.goals.interfaces.PingReceiver;
import g6Agent.services.Point;

public interface PingListener {
    void setPingReceiver(PingReceiver pingReceiver);

    void sendPing(Point ping);

    void receivePing(Percept message, String sender);
}
