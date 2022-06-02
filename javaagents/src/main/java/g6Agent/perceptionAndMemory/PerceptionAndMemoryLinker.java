package g6Agent.perceptionAndMemory;

import g6Agent.MailService;
import g6Agent.agents.Agent;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleSwarmSightControllerInterface;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;

/**
 * Class to Link all classes of PerceptionAndMemory
 */
public class PerceptionAndMemoryLinker {
    PerceptionAndMemoryImplementation perceptionAndMemory;
    SwarmSightController swarmSightController;


    public PerceptionAndMemoryLinker(Agent agent, MailService mailbox) {

        this.perceptionAndMemory = new PerceptionAndMemoryImplementation();
        this.swarmSightController = new SwarmSightController(mailbox, perceptionAndMemory, perceptionAndMemory, agent.getName());
        perceptionAndMemory.addLastActionListener(swarmSightController);
        perceptionAndMemory.setVisionReporter(swarmSightController);
    }

    public CommunicationModuleSwarmSightControllerInterface getSwarmSightController(){
        return this.swarmSightController;
    }
    public PerceptionAndMemory getPerceptionAndMemory(){
        return this.perceptionAndMemory;
    }
}
