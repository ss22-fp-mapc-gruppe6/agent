package g6Agent.agents;

import eis.iilang.*;
import g6Agent.perceptionAndMemory.Interfaces.AgentAgentMapCoordinaterInterface;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.MailService;
import g6Agent.actions.BasicActions;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;
import g6Agent.services.Point;




public class MyTestAgent extends Agent{
    private final PerceptionAndMemory perceptionAndMemory;
    private final AgentAgentMapCoordinaterInterface agentMapCoordinator;

    public MyTestAgent(String name, MailService mailbox){
        super(name, mailbox);
        PerceptionAndMemoryLinker pamLinker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = pamLinker.getPerceptionAndMemory();
        this.agentMapCoordinator = pamLinker.getAgentMapCoordinator();
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
        switch(message.getName()){
            case "MOVEMENT_NOTIFICATION"  -> agentMapCoordinator.processMovementNotification(message, sender);
            case "MY_VISION" -> agentMapCoordinator.processVisionNotificationNotification(message, sender);
        }
    }

}
