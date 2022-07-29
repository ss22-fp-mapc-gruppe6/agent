package g6Agent.perceptionAndMemory.messages;

import eis.iilang.*;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.Enties.AgentNameAndPosition;
import g6Agent.services.Point;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Message to Communicate Names and Positions of known Agents used by SwarmSightController
 * @param knownAgents the known Agents communicated
 * @param sender the sender
 *
 * @author Kai Müller
 */
public record KnownAgentsNotificationMessage(List<AgentNameAndPosition> knownAgents, String sender) {

    /**
     * Converts a Mail to an Entity if this Class
     * @param message the Message
     * @param sender the Sender
     * @return The KnownAgentsMessage
     */
    public static KnownAgentsNotificationMessage fromMail(Percept message, String sender) {
        if(! message.getName().equals(KnownAgentsNotificationMessage.identifier())) return null;
        List<AgentNameAndPosition> knownAgentsSend = new ArrayList<>();
        for (Parameter parameter : (ParameterList) message.getParameters().get(0)) {
            if (parameter instanceof Function function) {
                String name = function.getName();
                Point position = new Point(
                        ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                        ((Numeral) function.getParameters().get(1)).getValue().intValue()
                );
                knownAgentsSend.add(new AgentNameAndPosition(name, position));
                }
            }
        return new KnownAgentsNotificationMessage(knownAgentsSend, sender);
    }

    /**
     * Broadcasts the message over the Mailservice
     * @param mailservice the Mailservice
     */
    public void brodacast(MailService mailservice) {
        ParameterList listOfKnownAgentPercepts = convertToParameterListForKnownAgents(knownAgents);
        mailservice.broadcast(new Percept(KnownAgentsNotificationMessage.identifier(), listOfKnownAgentPercepts), sender);
    }


    @NotNull
    private ParameterList convertToParameterListForKnownAgents(List<AgentNameAndPosition> knownAgents) {
        ParameterList listOfKnownAgentPercepts = new ParameterList();
        for (AgentNameAndPosition agent : knownAgents) {
            if (!agent.name().equals(sender)) {
                listOfKnownAgentPercepts.add(new Function(agent.name(), new Numeral(agent.position().x), new Numeral(agent.position().y)));
            }
        }
        return listOfKnownAgentPercepts;
    }

    /**
     *
     * @return the Messsage Name, which identifies it.
     */
    public static String identifier(){
        return "KNOWN_AGENTS";
    }
}
