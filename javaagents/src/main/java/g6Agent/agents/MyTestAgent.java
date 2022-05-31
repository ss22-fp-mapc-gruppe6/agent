package g6Agent.agents;

import eis.iilang.*;
import g6Agent.actions.*;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.communicationModule.CommunicationModuleImplementation;
import g6Agent.decissionModule.DecisionModule;
import g6Agent.decissionModule.TheStupidestDecisionModule;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;


public class MyTestAgent extends Agent{

    private final PerceptionAndMemory perceptionAndMemory;
    private final DecisionModule decisionModule;
    //public HashMap<Integer, AgentStep> stepValues = new HashMap<>();
    //private GridObject grid;
    private final CommunicationModule communicationModule;

    public MyTestAgent(String name, MailService mailbox){
        super(name, mailbox);
        PerceptionAndMemoryLinker linker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = linker.getPerceptionAndMemory();
        this.communicationModule = new CommunicationModuleImplementation();
        this.communicationModule.addAgentMapCoordinator(linker.getAgentMapCoordinator());
        this.decisionModule = new TheStupidestDecisionModule(this.perceptionAndMemory);
    }

/*
    public Point getPosition(int step) {
        return stepValues.get(step).getAgentMapPos();
    }

    public GridObject getGrid() {
        return (grid);
    }
*/
    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        G6Action action = null;
        perceptionAndMemory.handlePercepts(getPercepts());
        if (perceptionAndMemory.isReadyForAction()){
            Goal currentGoal = decisionModule.revalidateGoal();
            action = currentGoal.getNextAction();
            communicationModule.broadcastActionAttempt((Action) action);

            say("DISPENSERS " + perceptionAndMemory.getDispensers().size());
            say("BLOCKS     " + perceptionAndMemory.getBlocks().size());
            say("ROLEZONES  " + perceptionAndMemory.getRoleZones().size());
            say("GOALZONES  " + perceptionAndMemory.getGoalZones().size());
            say("OBSTACLES  " + perceptionAndMemory.getObstacles().size());
            say("KNOWN AGENTS :  " + perceptionAndMemory.getKnownAgents().size());
            for (var agent : perceptionAndMemory.getKnownAgents()){
                System.out.println(agent);
            }
        }
        return (eis.iilang.Action) action;
    }



    @Override
    public void handleMessage(Percept message, String sender) {
        communicationModule.handleMessage(message, sender);
    }
/*
    public void updateGridPosition(Point vector, int step) {
        stepValues.get(step).getAgentMapPos().add(vector);

    }
*/

}
