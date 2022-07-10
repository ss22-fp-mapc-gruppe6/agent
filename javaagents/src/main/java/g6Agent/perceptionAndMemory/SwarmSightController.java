package g6Agent.perceptionAndMemory;

import eis.iilang.*;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.Enties.AgentNameAndPosition;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.perceptionAndMemory.Interfaces.*;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Controller for determining the relative Positions of other Agents and Communicating their Vision
 */
class SwarmSightController implements LastActionListener, CommunicationModuleSwarmSightControllerInterface, AgentVisionReporter {

    private final MailService mailservice;
    private final PerceptionAndMemory perceptionAndMemory;

    private final PerceptionAndMemoryInput perceptionAndMemoryInput;
    private final HashMap<String, StepAndMovement> attemptedMovements;

    private final SwarmSightModel swarmSightModel;
    private final String agentname;

    int messageCounter;
    private List<IntroductionRequest> requestsToAnswer;
    private List<IntroductionRequest> acceptMessagesThisStep;
    private final HashMap<String, Vison> visions;

    SwarmSightController(MailService mailservice, PerceptionAndMemory perceptionAndMemory, PerceptionAndMemoryInput perceptionAndMemoryInput, String agentname) {
        this.mailservice = mailservice;
        this.perceptionAndMemory = perceptionAndMemory;
        this.perceptionAndMemoryInput = perceptionAndMemoryInput;
        this.swarmSightModel = new SwarmSightModel();
        this.agentname = agentname;
        this.attemptedMovements = new HashMap<>();
        this.messageCounter = 0;
        this.requestsToAnswer = new ArrayList<>();
        this.acceptMessagesThisStep = new ArrayList<>();
        this.visions = new HashMap<>();
    }


    @Override
    public void processMovementNotification(Percept p, String sender) {
        if (!sender.equals(agentname)) {
            int step = ((Numeral) p.getParameters().get(1)).getValue().intValue();
            attemptedMovements.remove(sender);
            Movement movement = new Movement(
                    Direction.fromIdentifier((Identifier) p.getParameters().get(2)),
                    ((Numeral) p.getParameters().get(3)).getValue().intValue()
            );
            swarmSightModel.notifiedOfMovement(sender, movement);

        }

    }

    @Override
    public void broadcastActionAttempt(Action action) {
        if (action.getName().equals("move")) {
            mailservice.broadcast(new Percept("MOVEMENT_ATTEMPT",
                    new Numeral(messageCounter),                        //Clock
                    new Numeral(perceptionAndMemory.getCurrentStep()), // Step
                    action.getParameters().get(0),                     // Direction
                    new Numeral(determineyourOwnSpeed())                // Speed
            ), agentname);
        }
    }

    @Override
    public void processMovementAttempt(Percept message, String sender) {
        if (message.getName().equals("MOVEMENT_ATTEMPT")) {
            if (!sender.equals(agentname)) {
                attemptedMovements.put(sender, new StepAndMovement(
                        ((Numeral) message.getParameters().get(1)).getValue().intValue(),
                        new Movement(Direction.fromIdentifier((Identifier) message.getParameters().get(2)),
                                ((Numeral) message.getParameters().get(3)).getValue().intValue())));
            }
        }
    }

    @Override
    public void processIntroductionRequest(Percept message, String sender) {
        if (message.getName().equals("INTRODUCTION_REQUEST")) {
            if (!sender.equals(agentname)) {
                IntroductionRequest request = IntroductionRequest.fromMail(message, sender);
                if (request.step == perceptionAndMemory.getCurrentStep()) {
                    answerIntroductionRequest(request);
                } else {
                    this.requestsToAnswer.add(request);
                }
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
        if (message.getName().equals("INTRODUCTION_ACCEPT")) {
            IntroductionRequest acceptMessage = IntroductionRequest.fromMail(message, sender);
            this.acceptMessagesThisStep.add(acceptMessage);
        }
    }


    @Override
    public void reportLastAction(LastActionMemory lastAction) {
        //If moved at all
        if (lastAction.getName().equals("move")) {
            int speed = determineSpeedOfLastAction(lastAction);
            if (speed > 0) {
                Direction direction = Direction.fromIdentifier(((Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)));
                Movement movement = new Movement(direction, speed);
                swarmSightModel.movedMyself(movement);
                mailservice.broadcast(new Percept("MOVEMENT_NOTIFICATION",
                        new Numeral(messageCounter),
                        new Numeral(perceptionAndMemory.getCurrentStep()),
                        movement.direction().getIdentifier(),
                        new Numeral(movement.speed())
                ), agentname);
            }
        }
    }

    private int determineSpeedOfLastAction(LastActionMemory lastAction) {
        if (!(lastAction.getSuccessMessage().equals("success")
                || lastAction.getSuccessMessage().equals("partial_success"))) {
            return 0;
        }

        if (lastAction.getSuccessMessage().equals("partial_success")) {
            return 1;
            //TODO Explorer for whom it could be 1 or 2 is unhandeld
        } else {
            return determineyourOwnSpeed();
        }
    }

    private int determineyourOwnSpeed() {
        if (perceptionAndMemory.getCurrentRole() == null) return 1;
        int speed;
        if (perceptionAndMemory.getCurrentRole().getMovementSpeed().size() <= perceptionAndMemory.getDirectlyAttachedBlocks().size()) {  //logic to get array entry of speed
            speed = 0;
        } else {
            speed = perceptionAndMemory.getCurrentRole().getMovementSpeed().get(perceptionAndMemory.getDirectlyAttachedBlocks().size());
        }
        return speed;
    }

    @Override
    public void processVisionNotification(Percept message, String sender) {
        if (message.getName().equals("MY_VISION")) {
            if (!sender.equals(agentname)) {
                if (swarmSightModel.isKnown(sender)) {
                    List<Block> dispensers = new ArrayList<>();
                    List<Block> blocks = new ArrayList<>();
                    List<Point> roleZones = new ArrayList<>();
                    List<Point> goalZones = new ArrayList<>();
                    List<Point> obstacles = new ArrayList<>();
                    //parse Percept and write to lists, save as vision
                    for (Parameter parameter : (ParameterList) message.getParameters().get(0)) {
                        if (parameter instanceof Function function) {
                            switch (function.getName()) {
                                case "dispenser" -> dispensers.add(new Block(
                                        new Point(
                                                ((Numeral) function.getParameters().get(1)).getValue().intValue(),
                                                ((Numeral) function.getParameters().get(2)).getValue().intValue()
                                        ),
                                        ((Identifier) function.getParameters().get(0)).toProlog()
                                ));
                                case "block" -> blocks.add(new Block(
                                        new Point(
                                                ((Numeral) function.getParameters().get(1)).getValue().intValue(),
                                                ((Numeral) function.getParameters().get(2)).getValue().intValue()
                                        ),
                                        ((Identifier) function.getParameters().get(0)).toProlog()
                                ));
                                case "rolezone" -> roleZones.add(new Point(
                                        ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                                        ((Numeral) function.getParameters().get(1)).getValue().intValue()
                                ));
                                case "goalzone" -> goalZones.add(new Point(
                                        ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                                        ((Numeral) function.getParameters().get(1)).getValue().intValue()
                                ));
                                case "obstacle" -> obstacles.add(new Point(
                                        ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                                        ((Numeral) function.getParameters().get(1)).getValue().intValue()
                                ));
                            }
                        }
                    }
                    this.visions.put(sender, new Vison(dispensers, blocks, roleZones, goalZones, obstacles));
                }
            }
        }
    }


    record Vison(List<Block> dispensers, List<Block> blocks, List<Point> roleZones, List<Point> goalZones,
                 List<Point> obstacles) {
    }

    @Override
    public void reportMyVision(List<Block> dispensers, List<Block> blocks, List<Point> roleZones, List<Point> goalZones, List<Point> obstacles) {
        ParameterList functions = new ParameterList();
        for (Block dispenser : dispensers) {
            functions.add(new Function("dispenser", new Identifier(dispenser.getBlocktype()), new Numeral(dispenser.getCoordinates().x), new Numeral(dispenser.getCoordinates().y)));
        }

        for (Block dispenser : dispensers) {
            functions.add(new Function("block", new Identifier(dispenser.getBlocktype()), new Numeral(dispenser.getCoordinates().x), new Numeral(dispenser.getCoordinates().y)));
        }

        for (Point roleZone : roleZones) {
            functions.add(new Function("rolezone", new Numeral(roleZone.getX()), new Numeral(roleZone.getY())));
        }
        for (Point goalZone : goalZones) {
            functions.add(new Function("goalzone", new Numeral(goalZone.x), new Numeral(goalZone.y)));
        }
        for (Point obstacle : obstacles) {
            functions.add(new Function("obstacle", new Numeral(obstacle.x), new Numeral(obstacle.y)));
        }
        mailservice.broadcast(new Percept("MY_VISION", functions), agentname);
    }

    @Override
    public void updateMyVisionWithSightingsOfOtherAgents() {
        HashSet<Block> uniqueDispensers = new HashSet<>();
        HashSet<Block> uniqueBlocks = new HashSet<>();
        HashSet<Point> uniqueGoalZones = new HashSet<>();
        HashSet<Point> uniqueRoleZones = new HashSet<>();
        HashSet<Point> uniqueObstacles = new HashSet<>();
        for (AgentNameAndPosition agent : swarmSightModel.knownAgents()) {
            if (agent.position() != null) {
                Vison vison = visions.get(agent.name());
                if (vison != null) {
                    addVisionToHashSets(uniqueDispensers, uniqueBlocks, uniqueGoalZones, uniqueRoleZones, uniqueObstacles, agent, vison);
                }
            }
        }
        flushHashSetsToPerceptionAndMemory(uniqueDispensers, uniqueBlocks, uniqueGoalZones, uniqueRoleZones, uniqueObstacles);
    }

    private void flushHashSetsToPerceptionAndMemory(HashSet<Block> uniqueDispensers, HashSet<Block> uniqueBlocks, HashSet<Point> uniqueGoalZones, HashSet<Point> uniqueRoleZones, HashSet<Point> uniqueObstacles) {
        for (Block dispenser : uniqueDispensers) {
            try {
                addDispenserToPerception(dispenser);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding dispenser");
                e.printStackTrace();
            }
        }
        for (Block block : uniqueBlocks) {
            try {
                addBlockToPerception(block);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding block");
                e.printStackTrace();
            }
        }
        for (Point obstacle : uniqueObstacles) {
            try {
                addObstacleToPerception(obstacle);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding obstacle");
                e.printStackTrace();
            }
        }
        for (Point goalzone : uniqueGoalZones) {
            try {
                addGoalZoneToPerception(goalzone);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding goalZone");
                e.printStackTrace();
            }
        }
        for (Point roleZone : uniqueRoleZones) {
            try {
                addRoleZoneToPerception(roleZone);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding roleZone");
                e.printStackTrace();
            }
        }
    }

    private void addVisionToHashSets(HashSet<Block> uniqueDispensers, HashSet<Block> uniqueBlocks, HashSet<Point> uniqueGoalZones, HashSet<Point> uniqueRoleZones, HashSet<Point> uniqueObstacles, AgentNameAndPosition agent, Vison vison) {
        //add dispensers to uniqueDispensers
        for (Block dispenser : vison.dispensers()) {
            Block dispenserWithNewPosition =
                    new Block(dispenser.getCoordinates().add(agent.position()), dispenser.getBlocktype());
            if (isOutOfSight(dispenserWithNewPosition)) {
                uniqueDispensers.add(dispenserWithNewPosition);
            }
        }
        //add blocks to uniqueBlocks
        for (Block block : vison.blocks()) {
            Block blockWithNewPosition =
                    new Block(block.getCoordinates().add(agent.position()), block.getBlocktype());
            if (isOutOfSight(blockWithNewPosition)) {
                uniqueBlocks.add(blockWithNewPosition);
            }
        }
        //add goalzones to uniqueGoalzones
        for (Point goalZone : vison.goalZones()) {
            Point goalZoneWithNewPosition = goalZone.add(agent.position());
            if (isOutOfSight(goalZoneWithNewPosition)) {
                uniqueGoalZones.add(goalZoneWithNewPosition);
            }
        }
        //add rolezones to uniqueRoleZones
        for (Point roleZone : vison.roleZones()) {
            Point roleZoneWithNewPosition = roleZone.add(agent.position());
            if (isOutOfSight(roleZoneWithNewPosition)) {
                uniqueRoleZones.add(roleZoneWithNewPosition);
            }
        }
        //add obstacles to uniqueObstacles
        for (Point obstacle : vison.obstacles()) {
            Point obstacleWithNewPosition = obstacle.add(agent.position());
            if (isOutOfSight(obstacleWithNewPosition)) {
                uniqueObstacles.add(obstacleWithNewPosition);
            }
        }
    }

    private void addRoleZoneToPerception(Point roleZone) throws Exception {
        perceptionAndMemoryInput.handleRoleZone(new Percept(
                "roleZone",
                new Numeral(roleZone.x),
                new Numeral(roleZone.y)
        ));
    }

    private void addGoalZoneToPerception(Point goalzone) throws Exception {
        perceptionAndMemoryInput.handleGoalZone(new Percept(
                "goalZone",
                new Numeral(goalzone.x),
                new Numeral(goalzone.y)
        ));
    }

    private void addObstacleToPerception(Point obstacle) throws Exception {
        perceptionAndMemoryInput.handleThingPercept(
                new Percept("thing",
                        new Numeral(obstacle.x),
                        new Numeral(obstacle.y),
                        new Identifier("obstacle"),
                        new Identifier("")
                )
        );
    }

    private void addBlockToPerception(Block block) throws Exception {
        perceptionAndMemoryInput.handleThingPercept(
                new Percept("thing",
                        new Numeral(block.getCoordinates().x),
                        new Numeral(block.getCoordinates().y),
                        new Identifier("block"),
                        new Identifier(block.getBlocktype())
                )
        );
    }


    private void addDispenserToPerception(Block dispenser) throws Exception {
        perceptionAndMemoryInput.handleThingPercept(
                new Percept("thing",
                        new Numeral(dispenser.getCoordinates().x),
                        new Numeral(dispenser.getCoordinates().y),
                        new Identifier("dispenser"),
                        new Identifier(dispenser.getBlocktype())
                )
        );
    }

    private boolean isOutOfSight(Point point) {
        if (perceptionAndMemory.getCurrentRole() == null) return false; // if no Role ignore
        return point.manhattanDistanceTo(new Point(0, 0)) > perceptionAndMemory.getCurrentRole().getVisionRange();
    }

    private boolean isOutOfSight(Block block) {
        if (perceptionAndMemory.getCurrentRole() == null) return false; // if no Role ignore
        return block.getCoordinates().manhattanDistanceTo(new Point(0, 0)) > perceptionAndMemory.getCurrentRole().getVisionRange();
    }

    @Override
    public void handleStep() {
        handleUnanseweredRequests();
        this.requestsToAnswer = new ArrayList<>();

        //check accepts, each accept, which is a one of -> add sighting
        compareRequestsSendWithAccepts();
        this.acceptMessagesThisStep = new ArrayList<>();

        checkForOtherAgents();
        broadcastKnownAgents();
    }

    private void broadcastKnownAgents() {
        List<AgentNameAndPosition> knownAgents = swarmSightModel.knownAgents();
        if (!knownAgents.isEmpty()) {
            ParameterList listOfKnownAgentPercepts = new ParameterList();
            for (AgentNameAndPosition agent : knownAgents) {
                if (!agent.name().equals(agentname)) {
                    listOfKnownAgentPercepts.add(new Function(agent.name(), new Numeral(agent.position().y), new Numeral(agent.position().y)));
                }
            }
            mailservice.broadcast(new Percept("KNOWN_AGENTS", listOfKnownAgentPercepts), agentname);
        }
    }

    @Override
    public void processKnownAgentsNotification(Percept message, String sender) {
        if (message.getName().equals("KNOWN_AGENTS") && swarmSightModel.isKnown(sender)) {
            for (Parameter parameter : (ParameterList) message.getParameters().get(0)) {
                if (parameter instanceof Function function) {
                    String name = function.getName();
                    //if agent is not known add to known Agents with relative Position to sender
                    if (!swarmSightModel.isKnown(name) && !name.equals(agentname)) {
                        Point position = new Point(
                                ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                                ((Numeral) function.getParameters().get(1)).getValue().intValue()
                        );
                        swarmSightModel.heardOfAgentPosition(name, position, sender);
                    }
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

    private void handleUnanseweredRequests() {
        for (IntroductionRequest request : requestsToAnswer) {
            if (request.step == perceptionAndMemory.getCurrentStep()) {
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
            boolean isIdentified = comparePositionsInInternalMap(agentPosition);
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

    private boolean comparePositionWithMovementVectors(Point agentPosition, AgentNameAndPosition agentInMemory) {
        boolean isIdentified = false;
        StepAndMovement attemptedMove = attemptedMovements.get(agentInMemory.name());
        if (attemptedMove != null) {
            Point positionBefore = agentInMemory.position();
            Point positionAfter = agentInMemory.position().add(attemptedMove.movement.asVector());
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

    private record StepAndMovement(int step, Movement movement) {
    }

    /**
     * An request for if a agent is seen.
     *
     * @param clock    the clock
     * @param step     the step
     * @param position the inverted position from the sender. is the position the reciever should see the sender at.
     * @param sender   name of the sender
     */
    private record IntroductionRequest(int clock, int step, Point position, String sender) {


        void broadcast(MailService mailservice) {
            mailservice.broadcast(new Percept("INTRODUCTION_REQUEST",
                    new Numeral(this.clock), new Numeral(this.step), new Numeral(this.position.x), new Numeral(this.position.y)
            ), this.sender);
        }

        static IntroductionRequest fromMail(Percept percept, String sender) {
            return new IntroductionRequest(
                    ((Numeral) percept.getParameters().get(0)).getValue().intValue(), //clock
                    ((Numeral) percept.getParameters().get(1)).getValue().intValue(), //step
                    new Point(
                            ((Numeral) percept.getParameters().get(2)).getValue().intValue(),
                            ((Numeral) percept.getParameters().get(3)).getValue().intValue()
                    ),      //position
                    sender //sender
            );
        }


        void sendAccept(MailService mailService, String sender) {
            mailService.sendMessage(
                    new Percept(
                            "INTRODUCTION_ACCEPT",
                            new Numeral(this.clock), new Numeral(this.step), new Numeral(this.position.x), new Numeral(this.position.y)
                    ),
                    this.sender,
                    sender
            );
        }
    }
}
