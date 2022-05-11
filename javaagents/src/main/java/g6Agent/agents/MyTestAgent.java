package g6Agent.agents;

import eis.iilang.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;

import g6Agent.actions.Factory_g6Action;
import g6Agent.environment.GridObject;

import g6Agent.services.Direction;

import g6Agent.services.Point;
import g6Agent.services.AgentStep;

import java.util.HashMap;




public class MyTestAgent extends Agent{

    private final PerceptionAndMemory perceptionAndMemory;
    public HashMap<Integer, AgentStep> stepValues = new HashMap<>();
    private GridObject grid;


    public MyTestAgent(String name, MailService mailbox){
        super(name, mailbox);
        this.perceptionAndMemory = new PerceptionAndMemoryImplementation();
    }


    public Point getPosition(int step) {
        return stepValues.get(step).getAgentMapPos();
    }

    public GridObject getGrid() {
        return (grid);
    }

    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        Action action = null;
        perceptionAndMemory.handlePercepts(getPercepts());
        if (perceptionAndMemory.isReadyForAction()){
            //move
           
            // action = Actions.moveRandomly();
            // It will be more flexible when we use interface and factory method.
            action = Factory_g6Action.getActionClass("Move", Direction.SOUTH);


            //Deactivate Enemy Agents - seems to work, but with this logic leads to Agent hanging around enemy Agent
            //for (Point agentCoord : perceptionAndMemory.getEnemyAgents()){
            //    if (agentCoord.isAdjacent() && perceptionAndMemory.getEnergy() > 19){
            //        action = Actions.clear(agentCoord);
            //    }
            //}
            //Destroy Obstacles
            for (Point obstacle : perceptionAndMemory.getObstacles()){
                if (obstacle.isAdjacent() && perceptionAndMemory.getEnergy() > 19){
                    action = new Action("clear", new Numeral(obstacle.x), new Numeral(obstacle.y));
                    //action = Actions.clear(obstacle);
                }
            }
        }
        return action;
    }



    @Override
    public void handleMessage(Percept message, String sender) {

    }

    public void updateGridPosition(Point vector, int step) {
        stepValues.get(step).getAgentMapPos().add(vector);

    }


}
