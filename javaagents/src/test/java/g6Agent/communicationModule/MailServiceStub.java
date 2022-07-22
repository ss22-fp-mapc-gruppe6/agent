package g6Agent.communicationModule;

import eis.iilang.Percept;
import g6Agent.MailService;

import java.util.HashMap;
import java.util.Map;


/**
 * Class for testing communication
 */

public class MailServiceStub  extends MailService {
    private final Map<String, CommunicationModule> register = new HashMap<>();

    public void registerCommunicationModule(String agentname, CommunicationModule comModule){
        register.put(agentname, comModule);
    }
    @Override
    public void sendMessage(Percept message, String to, String from){

        CommunicationModule recipient = register.get(to);

        if(!(recipient == null)) {
            recipient.handleMessage(message, from);
        }
    }
    @Override
    public void broadcast(Percept message, String sender) {
        register.forEach((name, agent) -> {
                    if (!name.equals(sender)) {
                        System.out.println(message);
                        sendMessage(message, name, sender);
                    }
                }
        );
    }
}
