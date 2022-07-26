package g6Agent.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.actions.G6Action;
import g6Agent.actions.Move;
import g6Agent.actions.Skip;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.communicationModule.CommunicationModuleImplementation;
import g6Agent.decisionModule.DecisionModule;
import g6Agent.decisionModule.DecisionModuleImplementation;
import g6Agent.decisionModule.configurations.Tounament4Config;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Enties.AgentNameAndPosition;
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
        //this.decisionModule = new TheStupidestDecisionModule(this.perceptionAndMemory);
        this.decisionModule = new DecisionModuleImplementation(this.perceptionAndMemory, communicationModule,new Tounament4Config());
    }


    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        G6Action action = null;
        perceptionAndMemory.finishSync();
        if (perceptionAndMemory.isReadyForAction()) {
            Goal currentGoal = decisionModule.revalidateGoal();
            action = currentGoal.getNextAction();
/*
            say(currentGoal.getName());
            if(currentGoal.getName().equals("G6GoalDefendGoalZone")){
                say(action.toString() + " : " + action.predictSuccess(perceptionAndMemory));
            }
            */
            say("");
            if(action instanceof Move move && move.directions.size() > 1){
                System.out.println(move.directions);
            }
            if(perceptionAndMemory.getLastAction()!= null
                    && perceptionAndMemory.getLastAction().getName().equals("move")
            ){
                System.out.println("LastActionParameters : " + perceptionAndMemory.getLastAction().getParameters());
            }
            for (AgentNameAndPosition agentNameAndPosition : perceptionAndMemory.getKnownAgents()) {
                System.out.print(agentNameAndPosition.name() +" " + agentNameAndPosition.position() + ", ");
            }
            System.out.println();
            if(perceptionAndMemory.getEnergy() < 10) return new Skip();
        }
        return (eis.iilang.Action) action;
    }


    @Override
    public void handleMessage(Percept message, String sender) {
        communicationModule.handleMessage(message, sender);
    }

    @Override
    public void handlePerceptionforStep() {
        perceptionAndMemory.handlePercepts(getPercepts());
    }

    @Override
    public void initialiseSync() {
        perceptionAndMemory.initiateSync();
    }
    @Override
    public void handleSyncRequests() {
        perceptionAndMemory.handleSyncRequests();
    }

}
