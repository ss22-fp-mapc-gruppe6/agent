package g6Agent.perceptionAndMemory;

import eis.iilang.*;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.perceptionAndMemory.Interfaces.AgentVisionReporter;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleAgentMapCoordinatorInterface;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class AgentMapCoordinator implements LastActionListener, CommunicationModuleAgentMapCoordinatorInterface, AgentVisionReporter {

    private final MailService mailservice;
    private final PerceptionAndMemory perceptionAndMemory;

    private final HashMap<String, StepAndMovement> attemptedMovements;

    private final InternalMapOfOtherAgents internalMapOfOtherAgents;
    private final String agentname;

    int lambertClock;
    private List<IntroductionRequest> requestsSend;
    private List<IntroductionRequest> requestsToAnswer;
    private List<IntroductionRequest> acceptMessagesThisStep;

    AgentMapCoordinator(MailService mailservice, PerceptionAndMemory perceptionAndMemory, InternalMapOfOtherAgents internalMapOfOtherAgents, String agentname) {
        this.mailservice = mailservice;
        this.perceptionAndMemory = perceptionAndMemory;
        this.internalMapOfOtherAgents = internalMapOfOtherAgents;
        this.agentname = agentname;
        this.attemptedMovements = new HashMap<>();
        this.lambertClock = 0;
        this.requestsSend = new ArrayList<>();
        this.requestsToAnswer = new ArrayList<>();
        this.acceptMessagesThisStep = new ArrayList<>();
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
    public void processActionAttempt(Percept message, String sender) {
        if (message.getName().equals("MOVEMENT_ATTEMPT")) {
            setClockAfterRecieve(((Numeral) message.getParameters().get(0)).getValue().intValue());
            attemptedMovements.put(sender, new StepAndMovement(
                    ((Numeral) message.getParameters().get(1)).getValue().intValue(),
                    new Movement(Direction.fromIdentifier((Identifier)message.getParameters().get(2)),
                            ((Numeral) message.getParameters().get(3)).getValue().intValue())));
        }
    }

    @Override
    public void processIntroductionRequest(Percept message, String sender) {
        if(message.getName().equals("INTRODUCTION_REQUEST")) {
            IntroductionRequest request = IntroductionRequest.fromMail(message, sender);
            setClockAfterRecieve(request.clock());
            if (request.step == perceptionAndMemory.getCurrentStep()) {
                answerIntroductionRequest(request);
            } else {
                this.requestsToAnswer.add(request);
            }
        }
    }

    private void answerIntroductionRequest(IntroductionRequest request) {
        for (Point agentposition : perceptionAndMemory.getFriendlyAgents()) {
            if (agentposition.equals(request.position)) {
                request.sendAccept(mailservice, this.agentname);
            }
        }
    }

    @Override
    public void processIntroductionAccept(Percept message, String sender) {
        if(message.getName().equals("INTRODUCTION_ACCEPT")) {
            IntroductionRequest acceptMessage = IntroductionRequest.fromMail(message, sender);
            setClockAfterRecieve(acceptMessage.clock());
            this.acceptMessagesThisStep.add(acceptMessage);
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
    public void processVisionNotification(Percept message, String sender) {
        //TODO decipher the message and save the coordinates.
    }

    @Override
    public void reportMyVision(List<Block> dispensers, List<Point> roleZones, List<Point> goalZones, List<Point> obstacles) {
        //TODO broadcast everything as one big Percept -> message MY_VISION
    }

    @Override
    public void updateMyVisionWithSightingsOfOtherAgents() {
        //TODO get Visions saved (multiple hash maps?) of known Agents, add their relative positions and if they are not in Sight of the current Agent add them to the lists
    }

    @Override
    public void handleStep() {
        internalMapOfOtherAgents.incrementAllCounters();
        handleUnanseweredRequests();
        this.requestsToAnswer = new ArrayList<>();

        //check accepts, each accept, which is a one of -> add sighting
        compareRequestsSendWithAccepts();
        this.requestsSend = new ArrayList<>();
        this.acceptMessagesThisStep = new ArrayList<>();

        checkForOtherAgents();
    }

    private void handleUnanseweredRequests() {
        for (IntroductionRequest request : requestsToAnswer) {
            if (request.step == perceptionAndMemory.getCurrentStep()){
                answerIntroductionRequest(request);
            }
        }
    }

    private void compareRequestsSendWithAccepts() {
        HashMap<Integer, Integer> accepts = new HashMap<>();
        for (IntroductionRequest acceptMessage : acceptMessagesThisStep) {
            if(accepts.get(acceptMessage.clock()) == null){
                accepts.put(acceptMessage.clock(), 1);
            } else {
                int count = accepts.get(acceptMessage.clock());
                accepts.put(acceptMessage.clock(), (count + 1));
            }
        }
        accepts.forEach((key, value) -> {
            if (value != null){
                if (value == 1){
                    IntroductionRequest request = null;
                    for (IntroductionRequest requestSend : acceptMessagesThisStep){
                        if (requestSend.clock() == key){
                            request = requestSend;
                        }
                    }
                    if(request != null){
                        internalMapOfOtherAgents.spottetAgent(request.sender(), request.position().invert());
                    }
                }
            }
        });
    }


    private void checkForOtherAgents(){
        List<Point> unknownAgents = new ArrayList<>();
        //determine known Agents in sight
        for(Point agentPosition : perceptionAndMemory.getFriendlyAgents()){
            boolean isIdentified = comparePositionsInInternalMap(agentPosition);
            if(!isIdentified){
                unknownAgents.add(agentPosition);
            }
        }
        for(Point unknownAgent : unknownAgents){
            setClockBeforeSend();
            IntroductionRequest request = new IntroductionRequest(lambertClock, perceptionAndMemory.getCurrentStep(), unknownAgent.invert(), agentname);
            this.requestsSend.add(request);
            request.broadcast(mailservice);
        }
    }



    private boolean comparePositionsInInternalMap(Point agentPosition) {
        for(AgentNameAndPosition agentInMemory : internalMapOfOtherAgents.knownAgents()){
            //if movement is known
            boolean isIdentified = comparePositionWithMovementVectors(agentPosition, agentInMemory);
            if(isIdentified){
                internalMapOfOtherAgents.spottetAgent(agentInMemory.name(), agentPosition);
                return true;
            }
        }
        return false;
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

    private record StepAndMovement (int step, Movement movement){}

    /**
     * An request for if a agent is seen.
     * @param clock the clock
     * @param step the step
     * @param position the inverted position from the sender. is the position the reciever should see the sender at.
     * @param sender name of the sender
     */
    private record IntroductionRequest (int clock, int step, Point position, String sender){


        void broadcast(MailService mailservice) {
            mailservice.broadcast(new Percept("INTRODUCTION_REQUEST", new ParameterList(
            new Numeral(this.clock), new Numeral(this.step), new Numeral(this.position.x), new Numeral(this.position.y)
            )), this.sender);
        }

        static IntroductionRequest fromMail(Percept percept, String sender){
            return new IntroductionRequest(
                    ((Numeral)percept.getParameters().get(0)).getValue().intValue(), //clock
                    ((Numeral)percept.getParameters().get(1)).getValue().intValue(), //step
                    new Point(
                            ((Numeral)percept.getParameters().get(2)).getValue().intValue(),
                            ((Numeral)percept.getParameters().get(3)).getValue().intValue()
                    ),      //position
                    sender //sender
            );
        }


        void sendAccept(MailService mailService, String sender){
            mailService.sendMessage(
                    new Percept(
                            "INTRODUCTION_ACCEPT",
                            new ParameterList(new Numeral(this.clock), new Numeral(this.step), new Numeral(this.position.x), new Numeral(this.position.y))
                    ),
                    this.sender,
                    sender
            );
        }
    }
}
