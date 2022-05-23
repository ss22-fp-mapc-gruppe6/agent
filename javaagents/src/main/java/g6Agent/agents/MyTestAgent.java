package g6Agent.agents;

import eis.iilang.*;
import g6Agent.actions.*;
import g6Agent.decissionModule.DecisionModule;
import g6Agent.decissionModule.TheStupidestDecisionModule;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;


public class MyTestAgent extends Agent{

    private final PerceptionAndMemory perceptionAndMemory;
    private DecisionModule decisionModule;
    //public HashMap<Integer, AgentStep> stepValues = new HashMap<>();
    //private GridObject grid;


    public MyTestAgent(String name, MailService mailbox){
        super(name, mailbox);
        this.perceptionAndMemory = new PerceptionAndMemoryImplementation();
        this.decisionModule = new TheStupidestDecisionModule(perceptionAndMemory);
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
            for (Task t : perceptionAndMemory.getTasks()) {
            }
        }
        return (eis.iilang.Action) action;
    }



    @Override
    public void handleMessage(Percept message, String sender) {

    }
/*
    public void updateGridPosition(Point vector, int step) {
        stepValues.get(step).getAgentMapPos().add(vector);

    }
*/

}
