package g6Agent.perceptionAndMemory;

import g6Agent.MailService;
import g6Agent.agents.Agent;
import g6Agent.perceptionAndMemory.Interfaces.AgentAgentMapCoordinaterInterface;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

/**
 * Class to Link all classes of PerceptionAndMemory
 */
public class PerceptionAndMemoryLinker {
    PerceptionAndMemoryImplementation perceptionAndMemory;
    AgentMapCoordinator agentMapCoordinator;
    InternalMapOfOtherAgents internalMapOfOtherAgentsImplementation;

    public PerceptionAndMemoryLinker(Agent agent, MailService mailbox) {
        InternalMapOfOtherAgents internalMapOfOtherAgents = new InternalMapOfOtherAgents(agent.getName());
        this.internalMapOfOtherAgentsImplementation = internalMapOfOtherAgents;
        this.perceptionAndMemory = new PerceptionAndMemoryImplementation();
        this.agentMapCoordinator = new AgentMapCoordinator(mailbox, perceptionAndMemory, internalMapOfOtherAgents, agent.getName());
        perceptionAndMemory.addLastActionListener(agentMapCoordinator);
        perceptionAndMemory.setVisionReporter(agentMapCoordinator);
    }

    public AgentAgentMapCoordinaterInterface getAgentMapCoordinator(){
        return this.agentMapCoordinator;
    }
    public PerceptionAndMemory getPerceptionAndMemory(){
        return this.perceptionAndMemory;
    }
}
