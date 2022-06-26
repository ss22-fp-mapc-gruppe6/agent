package g6Agent.communicationModule.submodules;

import eis.iilang.Numeral;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.decisionModule.submodules.PingFilter;
import g6Agent.goals.PingReceiver;
import g6Agent.services.Point;

/**
 * Ping Listener to send and receive pings between agents which are working at the same task
 */
public class PingCommunicator implements PingListener{
    private PingReceiver receiver;
    private final String agentName;
    private final MailService mailservice;

    private PingFilter pingFilter;
    public PingCommunicator(String agentName, MailService mailservice) {
        this.agentName = agentName;
        this.mailservice = mailservice;
    }

    /**
     * Adds a filter to filter which agents recieve a ping
     * @param pingFilter
     */
    public void addPingFilter(PingFilter pingFilter){
        this.pingFilter = pingFilter;
    }

    @Override
    public void setPingReceiver(PingReceiver pingReceiver) {
       this.receiver = pingReceiver;
    }

    @Override
    public void sendPing(Point ping) {
        mailservice.broadcast(new Percept("PING", new Numeral(ping.x), new Numeral(ping.y)), agentName);
    }

    @Override
    public void receivePing(Percept message, String sender) {
        if (this.receiver != null && message.getName().equals("PING") && message.getParameters().size() == 2){
            Point point = new Point(
                    ((Numeral) message.getParameters().get(0)).getValue().intValue(),
                    ((Numeral) message.getParameters().get(1)).getValue().intValue()
            );
            if (pingFilter == null) {
                receiver.recievePing(point);
            } else{
                if (pingFilter.pingAccepted(point, sender)){
                    receiver.recievePing(point);
                }
            }

        }
    }
}
