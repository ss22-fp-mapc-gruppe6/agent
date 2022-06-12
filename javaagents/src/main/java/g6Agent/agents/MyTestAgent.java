package g6Agent.agents;

import eis.iilang.*;
import g6Agent.actions.Factory_g6Action;
import g6Agent.actions.Move;
import g6Agent.actions.Objects.Pair_BlockAgent;
import g6Agent.actions.g6Action;
import g6Agent.environment.GridObject;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.MailService;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.AgentStep;
import g6Agent.perceptionAndMemory.Enties.Task;
import java.util.HashMap;
import java.util.HashSet;


public class MyTestAgent extends Agent{
    PerceptionAndMemory perceptionAndMemory;
    public HashMap<Integer, AgentStep> stepValues = new HashMap<>();
    private GridObject grid;

    private  HashSet<Pair_BlockAgent> attachedList;

    private Task task;
    private int energy;
    private String roleName;

    public MyTestAgent(String name, MailService mailbox){
        super(name, mailbox);
        perceptionAndMemory = new PerceptionAndMemoryImplementation();
        setEnergy();
    }

    public Point getPosition(int step) {
        return stepValues.get(step).getAgentMapPos();
    }

    public GridObject getGrid() {
        return (grid);
    }
    public int getEnergy() {
        return energy;
    }

    public void setEnergy(){
        this.energy = perceptionAndMemory.getEnergy();
    }
    public void setRoleName(String roleName){
        this.roleName = roleName;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setAttachedList(HashSet list){this.attachedList = list;}

    public HashSet<Pair_BlockAgent> getAttachedList() {return this.attachedList;}

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
        stepValues.get(step).getAgentMapPos().translate(vector);

    }

}
