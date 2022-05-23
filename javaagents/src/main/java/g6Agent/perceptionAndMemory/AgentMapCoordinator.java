package g6Agent.perceptionAndMemory;

import eis.iilang.*;
import g6Agent.MailService;
import g6Agent.agents.Agent;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.perceptionAndMemory.Interfaces.AgentAgentMapCoordinaterInterface;
import g6Agent.perceptionAndMemory.Interfaces.AgentVisionReporter;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
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

    public void checkForOtherAgents(){
        List<Point> unknownAgents = new ArrayList<>();
        //determine known Agents in sight
        for(Point agentPosition : perceptionAndMemory.getFriendlyAgents()){
            boolean isIdentified = false;
            for(AgentNameAndPosition agentInMemory : internalMapOfOtherAgents.knownAgents()){
                //if movement is known
                isIdentified = comparePositionWithMovementVectors(agentPosition, agentInMemory);
                if(isIdentified){
                    internalMapOfOtherAgents.spottetAgent(agentInMemory.name(), agentPosition);
                    break;
                }
            }
            if(!isIdentified){
                unknownAgents.add(agentPosition);
            }
        }
        for(Point unknownAgent : unknownAgents){
            //TODO Send Message with Request for Answer if in position Vector
            //this agents position is fixed, the other Agent doesn't know yet if his movement succeeded
            //Encode with Lambert clock?
        }
    }

    private boolean comparePositionWithMovementVectors(Point agentPosition, AgentNameAndPosition agentInMemory) {
        boolean isIdentified = false;
        Movement attemptedMove = attemptedMovements.get(agentInMemory.name());
        if (attemptedMove != null){
            Point positionBefore = agentInMemory.position();
            Point positionAfter = agentInMemory.position().add(attemptedMove.asVector());
            if (positionBefore.x == positionAfter.x){
                int smallerY = (Math.min(positionBefore.y, positionAfter.y));
                int biggerY = (Math.max(positionBefore.y, positionAfter.y));
                if (agentPosition.x == positionBefore.x && (agentPosition.y >= smallerY && agentPosition.y <= biggerY)){
                    isIdentified = true;
                }
            }else{
                int smallerX = (Math.min(positionBefore.x, positionAfter.x));
                int biggerX = (Math.max(positionBefore.x, positionAfter.x));
                if (agentPosition.y == positionBefore.y && (agentPosition.x >= smallerX && agentPosition.x <= biggerX)){
                    isIdentified = true;
                }
            }
        }else{
           if (agentPosition.x == agentInMemory.position().x && agentPosition.y == agentInMemory.position().y){
               isIdentified = true;
           }
        }
        return isIdentified;
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
    public void deciferActionAttempt(Percept message, String sender) {
        if (message.getName().equals("MOVEMENT_ATTEMPT")) {
            attemptedMovements.put(sender,
                    new Movement(Direction.fromIdentifier((Identifier)message.getParameters().get(0)),
                            ((Numeral) message.getParameters().get(1)).getValue().intValue()));
        }
    }


    @Override
    public void reportLastAction(LastActionMemory lastAction){
        //If moved at all
        if (lastAction.getName().equals("move")){
            int speed = determineSpeedOfLastAction(lastAction);
            if (speed > 0){
                Direction direction = Direction.fromIdentifier(((Identifier) lastAction.getParameters().get(0)));
                Movement movement = new Movement(direction, speed);
                internalMapOfOtherAgents.movedMyself(movement);
                mailservice.broadcast(new Percept("MOVEMENT_NOTIFICATION", movement.asParameterList()), agentname);
            }
        }
    }

    private int determineSpeedOfLastAction(LastActionMemory lastAction) {
        if(!(lastAction.getSuccessMessage().equals("success")
                || lastAction.getSuccessMessage().equals("partial_success"))){
            return 0;
        }
        int speed;
        if (lastAction.getSuccessMessage().equals("partial_success")){
            speed = 1;
            //TODO Explorer for whom it could be 1 or 2 is unhandeld
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
