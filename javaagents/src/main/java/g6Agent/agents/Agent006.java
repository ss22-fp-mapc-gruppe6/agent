package g6Agent.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.actions.G6Action;
import g6Agent.brain.agentBrainModule;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.communicationModule.CommunicationModuleImplementation;
import g6Agent.decisionModule.DecisionModule;
import g6Agent.decisionModule.DecisionModuleImplementation;
import g6Agent.decisionModule.TheStupidestDecisionModule;
import g6Agent.decisionModule.configurations.Tounament4Config;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;


/**
 * The 2. best Agent
 */
public class Agent006 extends Agent {
    private final PerceptionAndMemory perceptionAndMemory;
    private final DecisionModule decisionModule;
    private final CommunicationModule communicationModule;


    /**
     * Constructor
     *
     * @param name    the agent's name
     * @param mailbox the mail facility
     */
    public Agent006(String name, MailService mailbox) {
        super(name, mailbox);
        PerceptionAndMemoryLinker linker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = linker.getPerceptionAndMemory();
        this.communicationModule = new CommunicationModuleImplementation(name, mailbox);
        this.communicationModule.addSwarmSightController(linker.getSwarmSightController());
        this.decisionModule = new DecisionModuleImplementation(this.perceptionAndMemory, communicationModule,new Tounament4Config());
    }

    /**
     * UNUSED ARTIFACT FROM BASIC AGENT
     * @param percept the percept to process
     */
    @Override
    public void handlePercept(Percept percept) {

    }

    /**
     * handles the new Perception of the Agent for this step
     */
    @Override
    public void handlePerceptionforStep() {
        perceptionAndMemory.handlePercepts(getPercepts());
    }

    /**
     * Initialises the Syncronization Process
     */
    @Override
    public void initialiseSync() {
        perceptionAndMemory.initiateSync();
    }

    /**
     * Handles Syncronization-Requests from other Agents
     */
    @Override
    public void handleSyncRequests() {
        perceptionAndMemory.handleSyncRequests();
    }


    @Override
    public Action step() {
        G6Action action = null;
        perceptionAndMemory.finishSync();
        if (perceptionAndMemory.isReadyForAction()) {
            Goal currentGoal = decisionModule.revalidateGoal();
            action = currentGoal.getNextAction();
        }
        return (eis.iilang.Action) action;
    }

    /**
     *
     * @param message the message that was sent
     * @param sender  name of the agent who sent the message
     */
    @Override
    public void handleMessage(Percept message, String sender) {
        communicationModule.handleMessage(message, sender);
    }
}
