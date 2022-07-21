package g6Agent.perceptionAndMemory;

import eis.iilang.*;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.Enties.*;
import g6Agent.perceptionAndMemory.messages.IntroductionRequest;
import g6Agent.perceptionAndMemory.messages.KnownAgentsNotificationMessage;
import g6Agent.perceptionAndMemory.messages.MovementNotificationMessage;
import g6Agent.perceptionAndMemory.messages.MyVisionMessage;
import g6Agent.perceptionAndMemory.Interfaces.*;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.SpeedCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Controller for determining the relative Positions of other Agents and Communicating their Vision
 *
 * @author Kai Müller
 */
class SwarmSightController implements LastActionListener, CommunicationModuleSwarmSightControllerInterface, AgentVisionReporter {

    private final MailService mailservice;
    private final PerceptionAndMemory perceptionAndMemory;
    private final HashMap<String, StepAndMovement> attemptedMovements;
    private final SwarmSightModel swarmSightModel;
    private final String agentname;
    private final SwarmSightDataConverter dataConverter;
    private List<IntroductionRequest> requestsToAnswer;
    private List<IntroductionRequest> acceptMessagesThisStep;
    private final HashMap<String, Vision> visions;
    int messageCounter;

    SwarmSightController(MailService mailservice, PerceptionAndMemory perceptionAndMemory, PerceptionAndMemoryInput perceptionAndMemoryInput, String agentname) {
        this.mailservice = mailservice;
        this.perceptionAndMemory = perceptionAndMemory;
        this.dataConverter = new SwarmSightDataConverter(perceptionAndMemoryInput, perceptionAndMemory);
        this.swarmSightModel = new SwarmSightModel();
        this.agentname = agentname;
        this.attemptedMovements = new HashMap<>();
        this.messageCounter = 0;
        this.requestsToAnswer = new ArrayList<>();
        this.acceptMessagesThisStep = new ArrayList<>();
        this.visions = new HashMap<>();
    }

    @Override
    public void processMovementNotification(Percept message, String sender) {
        if (!sender.equals(agentname)) {
            MovementNotificationMessage notificationMessage = MovementNotificationMessage.fromMail(message, sender);
            if(notificationMessage == null) return;
            attemptedMovements.remove(sender);
            Movement movement = new Movement(
                    parameterlistToListOfDirections((ParameterList) notificationMessage.movementParameter()), //directions
                    notificationMessage.speed()        //speed
            );
            swarmSightModel.notifiedOfMovement(sender, movement);
        }
    }

    @Override
    public void broadcastActionAttempt(@NotNull Action action) {
        if (action.getName().equals("move")) {
            ParameterList parameters = new ParameterList(action.getParameters());

            mailservice.broadcast(new Percept("MOVEMENT_ATTEMPT",
                    new Numeral(messageCounter),                        //Clock
                    new Numeral(perceptionAndMemory.getCurrentStep()), // Step
                    parameters,                                        // Directions
                    new Numeral(SpeedCalculator.calculateSpeed(perceptionAndMemory)) // Speed
            ), agentname);
        }
    }

    @Override
    public void processMovementAttempt(@NotNull Percept message, String sender) {
        if (message.getName().equals("MOVEMENT_ATTEMPT")) {
            if (!sender.equals(agentname)) {
                attemptedMovements.put(sender, new StepAndMovement(
                        ((Numeral) message.getParameters().get(1)).getValue().intValue(),
                        new Movement(parameterlistToListOfDirections((ParameterList) message.getParameters().get(2)),
                                ((Numeral) message.getParameters().get(3)).getValue().intValue())));
            }
        }
    }

    @NotNull
    private List<Direction> parameterlistToListOfDirections(@NotNull ParameterList directionIdentifiers) {
        List<Direction> directions = new ArrayList<>();
        for (Parameter p : directionIdentifiers) {
            directions.add(Direction.fromIdentifier((Identifier) p));
        }
        return directions;
    }

    @Override
    public void processIntroductionRequest(@NotNull Percept message, String sender) {
        if (message.getName().equals("INTRODUCTION_REQUEST")) {
            if (!sender.equals(agentname)) {
                IntroductionRequest request = IntroductionRequest.fromMail(message, sender);
                if (request.step() == perceptionAndMemory.getCurrentStep()) {
                    answerIntroductionRequest(request);
                } else {
                    this.requestsToAnswer.add(request);
                }
            }
        }
    }

    private void answerIntroductionRequest(IntroductionRequest request) {
        for (Point agentposition : perceptionAndMemory.getFriendlyAgents()) {
            if (agentposition.equals(request.position())) {
                request.sendAccept(mailservice, this.agentname);
            }
        }
    }

    @Override
    public void processIntroductionAccept(@NotNull Percept message, String sender) {
        if (message.getName().equals("INTRODUCTION_ACCEPT")) {
            IntroductionRequest acceptMessage = IntroductionRequest.fromMail(message, sender);
            this.acceptMessagesThisStep.add(acceptMessage);
        }
    }

    @Override
    public void reportLastAction(@NotNull LastActionMemory lastAction) {
        //If moved at all
        if (lastAction.getName().equals("move")) {
            int speed = SpeedCalculator.determineSpeedOfLastAction(lastAction, perceptionAndMemory);
            if (speed > 0) {
                MovementNotificationMessage movementNotificationMessage
                        = new MovementNotificationMessage(
                                messageCounter,
                                perceptionAndMemory.getCurrentStep(),
                                lastAction.getParameters().get(0),
                                speed,
                                agentname);
                List<Direction> directions = parameterlistToListOfDirections((ParameterList) lastAction.getParameters().get(0));
                Movement movement = new Movement(directions, speed);
                swarmSightModel.movedMyself(movement);
                movementNotificationMessage.broadcast(mailservice);
            }
        }
    }

    @Override
    public void processVisionNotification(@NotNull Percept message, String sender) {
        if (message.getName().equals("MY_VISION")) {
            if (!sender.equals(agentname)) {
                if (swarmSightModel.isKnown(sender)) {
                    MyVisionMessage receivedVisionMessage = MyVisionMessage.fromMail(message, sender);
                    if (receivedVisionMessage != null) {
                        this.visions.put(sender, receivedVisionMessage.vision());
                    }
                }
            }
        }
    }

    @Override
    public void reportMyVision(Vision vision) {
        MyVisionMessage myVisionMessage = new MyVisionMessage(vision, agentname);
        myVisionMessage.broadcast(mailservice);
    }

    @Override
    public void updateMyVisionWithSightingsOfOtherAgents() {
        dataConverter.updateMyVisionWithSightingsOfOtherAgents(swarmSightModel, visions);
    }

    @Override
    public void handleStep() {
        handleUnansweredRequests();
        this.requestsToAnswer = new ArrayList<>();
        //check accepts, each accept, which is a one of -> add sighting
        compareRequestsSendWithAccepts();
        this.acceptMessagesThisStep = new ArrayList<>();

        checkForOtherAgents();
        //broadcastKnownAgents();
    }

    private void broadcastKnownAgents() {
        List<AgentNameAndPosition> knownAgents = swarmSightModel.knownAgents();
        if (!knownAgents.isEmpty()) {
            KnownAgentsNotificationMessage knownAgentsMessage = new KnownAgentsNotificationMessage(knownAgents, this.agentname);
            knownAgentsMessage.brodacast(mailservice);
        }
    }

    @Override
    public void processKnownAgentsNotification(@NotNull Percept message, String sender) {
        if (message.getName().equals(KnownAgentsNotificationMessage.identifier()) && swarmSightModel.isKnown(sender)) {
            KnownAgentsNotificationMessage knownAgentsMessage = KnownAgentsNotificationMessage.fromMail(message, sender);
            if (knownAgentsMessage == null) return;
            for (AgentNameAndPosition agentInfo : knownAgentsMessage.knownAgents()) {
                //if agent is not known add to known Agents with relative Position to sender
                if (!swarmSightModel.isKnown(agentInfo.name()) && !agentInfo.name().equals(agentname)) {
                    swarmSightModel.heardOfAgentPosition(agentInfo.name(), agentInfo.position(), sender);
                }
            }
        }
    }

    @Override
    public List<AgentNameAndPosition> getKnownAgentPositions() {
        return swarmSightModel.knownAgents();
    }

    @Override
    public Point getPositionOfAgent(String agentname) {
        return swarmSightModel.getAgentPosition(agentname);
    }

    private void handleUnansweredRequests() {
        for (IntroductionRequest request : requestsToAnswer) {
            if (request.step() == perceptionAndMemory.getCurrentStep()) {
                answerIntroductionRequest(request);
            }
        }
    }

    private void compareRequestsSendWithAccepts() {
        HashMap<Integer, Integer> accepts = new HashMap<>();
        countAcceptMessages(accepts);
        accepts.forEach((key, value) -> {
            if (value != null) {
                if (value == 1) {
                    acceptOneOf(key);
                }
            }
        });
    }

    private void acceptOneOf(Integer key) {
        IntroductionRequest acceptMessage = null;
        for (IntroductionRequest acceptSend : acceptMessagesThisStep) {
            if (acceptSend.clock() == key) {
                acceptMessage = acceptSend;
            }
        }
        if (acceptMessage != null) {
            swarmSightModel.spottetAgent(acceptMessage.sender(), acceptMessage.position().invert());
        }
    }

    private void countAcceptMessages(HashMap<Integer, Integer> accepts) {
        for (IntroductionRequest acceptMessage : acceptMessagesThisStep) {
            if (accepts.get(acceptMessage.clock()) == null) {
                accepts.put(acceptMessage.clock(), 1);
            } else {
                int count = accepts.get(acceptMessage.clock());
                accepts.put(acceptMessage.clock(), (count + 1));
            }
        }
    }

    private void checkForOtherAgents() {
        List<Point> unknownAgents = new ArrayList<>();
        //determine known Agents in sight
        for (Point agentPosition : perceptionAndMemory.getFriendlyAgents()) {
            boolean isIdentified = comparePositionsInInternalMap(agentPosition); //TODO Check function!
            //boolean isIdentified = false; //always ask!
            if (!isIdentified) {
                unknownAgents.add(agentPosition);
            }
        }
        for (Point unknownAgent : unknownAgents) {
            if (!unknownAgent.equals(new Point(0, 0))) { //is not self
                messageCounter = messageCounter + 1;
                IntroductionRequest request = new IntroductionRequest(messageCounter, perceptionAndMemory.getCurrentStep(), unknownAgent.invert(), agentname);
                request.broadcast(mailservice);
            }
        }
    }

    private boolean comparePositionsInInternalMap(Point agentPosition) {
        for (AgentNameAndPosition agentInMemory : swarmSightModel.knownAgents()) {
            //if movement is known
            boolean isIdentified = comparePositionWithMovementVectors(agentPosition, agentInMemory);
            if (isIdentified) {
                swarmSightModel.spottetAgent(agentInMemory.name(), agentPosition);
                return true;
            }
        }
        return false;
    }

    private boolean comparePositionWithMovementVectors(Point agentPosition, @NotNull AgentNameAndPosition agentInMemory) {
        boolean isIdentified = false;
        StepAndMovement attemptedMove = attemptedMovements.get(agentInMemory.name());
        if (attemptedMove != null) {
            Point positionBefore = agentInMemory.position();
            Point positionAfter = agentInMemory.position().add(attemptedMove.movement().asVector());
            if (positionBefore.x == positionAfter.x) {
                int smallerY = (Math.min(positionBefore.y, positionAfter.y));
                int biggerY = (Math.max(positionBefore.y, positionAfter.y));
                if (agentPosition.x == positionBefore.x && (agentPosition.y >= smallerY && agentPosition.y <= biggerY)) {
                    isIdentified = true;
                }
            } else {
                int smallerX = (Math.min(positionBefore.x, positionAfter.x));
                int biggerX = (Math.max(positionBefore.x, positionAfter.x));
                if (agentPosition.y == positionBefore.y && (agentPosition.x >= smallerX && agentPosition.x <= biggerX)) {
                    isIdentified = true;
                }
            }
        } else {
            if (agentPosition.x == agentInMemory.position().x && agentPosition.y == agentInMemory.position().y) {
                isIdentified = true;
            }
        }
        return isIdentified;
    }
}
