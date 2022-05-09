package g6Agent.agents;

import eis.iilang.*;
import g6Agent.perceptionAndMemory.Interfaces.AgentAgentMapCoordinaterInterface;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.MailService;
import g6Agent.actions.BasicActions;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;
import g6Agent.services.Point;




public class MyTestAgent extends Agent{
    private final PerceptionAndMemory perceptionAndMemory;

    public MyTestAgent(String name, MailService mailbox){
        super(name, mailbox);
        this.perceptionAndMemory = new PerceptionAndMemoryImplementation();
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
            action = BasicActions.moveRandomly();
            //Deactivate Enemy Agents - seems to work, but with this logic leads to Agent hanging around enemy Agent
            //for (Point agentCoord : perceptionAndMemory.getEnemyAgents()){
            //    if (agentCoord.isAdjacent() && perceptionAndMemory.getEnergy() > 19){
            //        action = Actions.clear(agentCoord);
            //    }
            //}
            //Destroy Obstacles
            for (Point obstacle : perceptionAndMemory.getObstacles()){
                if (obstacle.isAdjacent() && perceptionAndMemory.getEnergy() > 19){
                    action = BasicActions.clear(obstacle);
                }
            }
        }
        return action;
    }



    @Override
    public void handleMessage(Percept message, String sender) {

    }
}
