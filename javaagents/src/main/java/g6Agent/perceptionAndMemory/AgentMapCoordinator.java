package g6Agent.perceptionAndMemory;

import eis.iilang.*;
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

import java.util.HashMap;
import java.util.List;

class AgentMapCoordinator implements LastActionListener, AgentAgentMapCoordinaterInterface, AgentVisionReporter {

    private final MailService mailservice;
    private final PerceptionAndMemory perceptionAndMemory;

    private final HashMap<String, Movement> attemptedMovements;

    private final InternalMapOfOtherAgents internalMapOfOtherAgents;
    private final String agentname;

    AgentMapCoordinator(MailService mailservice, PerceptionAndMemory perceptionAndMemory, InternalMapOfOtherAgents internalMapOfOtherAgents, String agentname) {
        this.mailservice = mailservice;
        this.perceptionAndMemory = perceptionAndMemory;
        this.internalMapOfOtherAgents = internalMapOfOtherAgents;
        this.agentname = agentname;
        this.attemptedMovements = new HashMap<>();
    }

    @Override
    public void processMovementNotification(Percept p, String sender){
        attemptedMovements.put(sender, null);
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
    public void processVisionNotification(Percept message, String sender) {
        //TODO decipher the message and save the coordinates.
    }

    @Override
    public void broadcastActionAttempt(Action action) {
        if (action.getName().equals("move")){
            mailservice.broadcast(new Percept("MOVEMENT_ATTEMPT", new ParameterList(action.getParameters().get(0), new Numeral(determineSpeed()))), agentname);
        }
    }

    @Override
    public void deciferActionAttemot(Percept message, String sender) {
        if (message.getName().equals("MOVEMENT_ATTEMPT")) {
            attemptedMovements.put(sender,
                    new Movement(Direction.fromIdentifier((Identifier)message.getParameters().get(0)),
                            ((Numeral) message.getParameters().get(1)).getValue().intValue()));
        }
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
                speed = determineSpeed();
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

    private int determineSpeed(LastActionMemory lastAction) {
        int speed;
        if (lastAction.getSuccessMessage().equals("partial_success")){
            speed = 1;
            //TODO check if any Agent role has more than speed 2, this is unhandled
        } else {
            speed = determineSpeed();
        }
        return speed;
    }

    private int determineSpeed() {
        int speed;
        if (perceptionAndMemory.getCurrentRole().getMovementSpeed().size() <= perceptionAndMemory.getAttached().size()){
            speed = 0;
        } else {
            speed = perceptionAndMemory.getCurrentRole().getMovementSpeed().get(perceptionAndMemory.getAttached().size());
        }
        return speed;
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
