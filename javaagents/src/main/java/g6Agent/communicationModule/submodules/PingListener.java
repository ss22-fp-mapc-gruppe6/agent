package g6Agent.communicationModule.submodules;

import eis.iilang.Percept;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.goals.PingReceiver;
import g6Agent.services.Point;

public interface PingListener {
    public void setPingReceiver(PingReceiver pingReceiver);

    public void sendPing(Point ping);

    public void receivePing(Percept message, String sender);
}
