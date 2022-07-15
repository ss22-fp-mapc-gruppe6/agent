package g6Agent.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.actions.G6Action;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.communicationModule.CommunicationModuleImplementation;
import g6Agent.decisionModule.DecisionModule;
import g6Agent.decisionModule.DecisionModuleImplementation;
import g6Agent.decisionModule.TheStupidestDecisionModule;
import g6Agent.decisionModule.configurations.NewTestConfig;
import g6Agent.goals.G6GoalFulfillSingleTaskV1;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;

public class MyTestAgent2 extends Agent {
    private final PerceptionAndMemory perceptionAndMemory;
    private final DecisionModule decisionModule;

    private final CommunicationModule communicationModule;

    public MyTestAgent2(String name, MailService mailbox) {
        super(name, mailbox);
        PerceptionAndMemoryLinker linker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = linker.getPerceptionAndMemory();
        this.communicationModule = new CommunicationModuleImplementation(name, mailbox);
        this.communicationModule.addSwarmSightController(linker.getSwarmSightController());
        this.decisionModule = new TheStupidestDecisionModule(this.perceptionAndMemory);
        //this.decisionModule = new DecisionModuleImplementation(this.perceptionAndMemory, communicationModule,new NewTestConfig());
    }


    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        G6Action action = null;
        perceptionAndMemory.handlePercepts(getPercepts());
        if (perceptionAndMemory.isReadyForAction()) {
            Goal currentGoal = decisionModule.revalidateGoal();
            action = currentGoal.getNextAction();
            communicationModule.broadcastActionAttempt((Action) action);
        }
        return (eis.iilang.Action) action;
    }


    @Override
    public void handleMessage(Percept message, String sender) {
        communicationModule.handleMessage(message, sender);
    }

}
