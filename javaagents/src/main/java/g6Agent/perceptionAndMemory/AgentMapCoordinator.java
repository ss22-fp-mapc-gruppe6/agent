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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class AgentMapCoordinator implements LastActionListener, AgentAgentMapCoordinaterInterface, AgentVisionReporter {

    private final MailService mailservice;
    private final PerceptionAndMemory perceptionAndMemory;

    private final HashMap<String, StepAndMovement> attemptedMovements;

    private final InternalMapOfOtherAgents internalMapOfOtherAgents;
    private final String agentname;

    int lambertClock;

    AgentMapCoordinator(MailService mailservice, PerceptionAndMemory perceptionAndMemory, InternalMapOfOtherAgents internalMapOfOtherAgents, String agentname) {
        this.mailservice = mailservice;
        this.perceptionAndMemory = perceptionAndMemory;
        this.internalMapOfOtherAgents = internalMapOfOtherAgents;
        this.agentname = agentname;
        this.attemptedMovements = new HashMap<>();
        this.lambertClock = 0;
    }

    private void setClockBeforeSend(){
        lambertClock = lambertClock +1;
    }
    private  void setClockAfterRecieve(int timestamp){
        this.lambertClock = Math.max(lambertClock, timestamp) + 1;
    }

    @Override
    public void processMovementNotification(Percept p, String sender){
        setClockAfterRecieve(((Numeral)p.getParameters().get(0)).getValue().intValue());
        int step = ((Numeral)p.getParameters().get(1)).getValue().intValue();
        attemptedMovements.put(sender, null);
        InternalMapEntry entry = internalMapOfOtherAgents.getAgentPosition(sender);
        if (entry != null){
            Movement movement = new Movement(
                    Direction.fromIdentifier((Identifier)p.getParameters().get(2)),
                     ((Numeral) p.getParameters().get(3)).getValue().intValue()
                    );
            Point nextPositon = entry.getPosition().add(movement.asVector());
            entry.setPosition(nextPositon);
            entry.increaseCounter();
        }
    }

    public void checkForOtherAgents(){
        List<Point> unknownAgents = new ArrayList<>();
        //determine known Agents in sight
        for(Point agentPosition : perceptionAndMemory.getFriendlyAgents()){
            boolean isIdentified = comparePositionsInInternalMap(agentPosition);
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

    private boolean comparePositionsInInternalMap(Point agentPosition) {
        boolean isIdentified = false;
        for(AgentNameAndPosition agentInMemory : internalMapOfOtherAgents.knownAgents()){
            //if movement is known
            isIdentified = comparePositionWithMovementVectors(agentPosition, agentInMemory);
            if(isIdentified){
                internalMapOfOtherAgents.spottetAgent(agentInMemory.name(), agentPosition);
                break;
            }
        }
        return isIdentified;
    }

    private boolean comparePositionWithMovementVectors(Point agentPosition, AgentNameAndPosition agentInMemory) {
        boolean isIdentified = false;
        StepAndMovement attemptedMove = attemptedMovements.get(agentInMemory.name());
        if (attemptedMove != null){
            Point positionBefore = agentInMemory.position();
            Point positionAfter = agentInMemory.position().add(attemptedMove.movement.asVector());
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
            setClockBeforeSend();
            mailservice.broadcast(new Percept("MOVEMENT_ATTEMPT",
                    new ParameterList(
                            new Numeral(lambertClock),                          //Lambert Clock
                            new Numeral(perceptionAndMemory.getCurrentStep()), // Step
                            action.getParameters().get(0),                     // Direction
                            new Numeral(determineyourOwnSpeed())
                    )
            ), agentname);// Speed
        }
    }

    @Override
    public void deciferActionAttempt(Percept message, String sender) {
        if (message.getName().equals("MOVEMENT_ATTEMPT")) {
            setClockAfterRecieve(((Numeral) message.getParameters().get(0)).getValue().intValue());
            attemptedMovements.put(sender, new StepAndMovement(
                    ((Numeral) message.getParameters().get(1)).getValue().intValue(),
                    new Movement(Direction.fromIdentifier((Identifier)message.getParameters().get(2)),
                            ((Numeral) message.getParameters().get(3)).getValue().intValue())));
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
                setClockBeforeSend();
                mailservice.broadcast(new Percept("MOVEMENT_NOTIFICATION", new ParameterList(
                        new Numeral(lambertClock),
                        new Numeral(perceptionAndMemory.getCurrentStep()),
                        movement.direction().getIdentifier(),
                        new Numeral(movement.speed())
                )), agentname);
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
            speed = determineyourOwnSpeed();
        }
        return speed;
    }

    private int determineyourOwnSpeed() {
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

    record StepAndMovement (int step, Movement movement){}
}
