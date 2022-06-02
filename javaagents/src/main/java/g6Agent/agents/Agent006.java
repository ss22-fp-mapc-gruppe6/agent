package g6Agent.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.actions.G6Action;
import g6Agent.decissionModule.DecisionModule;
import g6Agent.decissionModule.TheStupidestDecisionModule;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;

import java.util.Collection;
import java.util.List;


/**
 * The 2. best Agent
 */
public class Agent006 extends Agent{
    private final PerceptionAndMemory perceptionAndMemory;
    private final DecisionModule decisionModule;


    /**
     * Constructor
     *
     * @param name    the agent's name
     * @param mailbox the mail facility
     */
    public Agent006(String name, MailService mailbox) {
        super(name, mailbox);
        this.perceptionAndMemory = new PerceptionAndMemoryImplementation();
        this.decisionModule = new TheStupidestDecisionModule(perceptionAndMemory);
    }

    @Override
    public void handlePercept(Percept percept) {
        System.out.println("Agent006.handlePercept");
        System.out.println("percept = " + percept);

    }

    @Override
    public Action step() {
        final var percepts = getPercepts();
        return step(percepts);
    }

    @Override
    public void handleMessage(Percept message, String sender) {

    }

    public Action step(List<Percept> percepts){
        G6Action action = null;
        perceptionAndMemory.handlePercepts(percepts);
//        if (perceptionAndMemory.isReadyForAction()){
            Goal currentGoal = decisionModule.revalidateGoal();
            action = currentGoal.getNextAction();
//        }
        return (eis.iilang.Action) action;
    }
}
