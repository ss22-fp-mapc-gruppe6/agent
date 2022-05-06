package g6Agent.perceptionAndMemory;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.perceptionAndMemory.Interfaces.AgentAgentMapCoordinaterInterface;
import g6Agent.perceptionAndMemory.Interfaces.AgentVisionReporter;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.List;

class AgentMapCoordinator implements LastActionListener, AgentAgentMapCoordinaterInterface, AgentVisionReporter {

    private final MailService mailservice;
    private final PerceptionAndMemory perceptionAndMemory;

    private final InternalMapOfOtherAgents internalMapOfOtherAgents;
    private final String agentname;

    AgentMapCoordinator(MailService mailservice, PerceptionAndMemory perceptionAndMemory, InternalMapOfOtherAgents internalMapOfOtherAgents, String agentname) {
        this.mailservice = mailservice;
        this.perceptionAndMemory = perceptionAndMemory;
        this.internalMapOfOtherAgents = internalMapOfOtherAgents;
        this.agentname = agentname;
    }

    @Override
    public void processMovementNotification(Percept p, String sender){
        InternalMapEntry entry = internalMapOfOtherAgents.getAgentPosition(sender);
        if (entry != null){
            Movement movement = new Movement(
                    Direction.fromIdentifier((Identifier)p.getParameters().get(0)),
                     ((Numeral) p.getParameters().get(1)).getValue().intValue()
                    );
            Point nextPosíton = entry.getPosition().add(movement.asVector());
            entry.setPosition(nextPosíton);
            entry.increaseCounter();
        }
    }

    @Override
    public void processVisionNotificationNotification(Percept message, String sender) {
        //TODO decipher the message and save the coordinates.
    }

    @Override
    public void reportLastAction(LastActionMemory lastAction){
        if (lastAction.getName().equals("move")
                &&
                (lastAction.getSuccessMessage().equals("success")
                        || lastAction.getSuccessMessage().equals("partial_success"))){
            int speed;
            if (lastAction.getSuccessMessage().equals("partial_success")){
                speed = 1;
                //TODO check if any Agent role has more than speed 2, this is unhandled
            } else {
                if (perceptionAndMemory.getCurrentRole().getMovementSpeed().size() <= perceptionAndMemory.getAttached().size()){
                    speed = 0;
                } else {
                    speed = perceptionAndMemory.getCurrentRole().getMovementSpeed().get(perceptionAndMemory.getAttached().size());
                }
            }
            System.out.println("SPEED CHECK : " + speed);
            if (speed > 0){
                Direction direction = Direction.fromIdentifier(((Identifier) lastAction.getParameters().get(0)));
                Movement movement = new Movement(direction, speed);
                internalMapOfOtherAgents.movedMyself(movement);
                mailservice.broadcast(new Percept("MOVEMENT_NOTIFICATION", movement.asParameterList()), agentname);
            }
        }
    }

    @Override
    public void reportMyVision(List<Block> dispensers, List<Point> roleZones, List<Point> goalZones, List<Point> obstacles) {
        //TODO broadcast everything as one big Percept -> message MY_VISION
    }

    @Override
    public void updateMyVisionWithSightingsOfOtherAgents() {
        //TODO get Visions saved (multiple hash maps?) of known Agents, add their relative positions and if they are not in Sight of the current Agent add them to the lists
    }
}
