package g6Agent.agents;

import eis.iilang.*;
import g6Agent.MailService;
import g6Agent.actions.G6Action;
import g6Agent.actions.Move;
import g6Agent.actions.Skip;
import g6Agent.communicationModule.CommunicationModuleImplementation;
import g6Agent.decisionModule.DecisionModuleImplementation;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.decisionModule.configurations.Tounament4Config;
import g6Agent.goals.G6GoalChangeRole;
import g6Agent.goals.G6GoalExploreV2;
import g6Agent.goals.Goal;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.SpeedCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovementTestAgent extends Agent {
    private final PerceptionAndMemory perceptionAndMemory;
    private final CommunicationModuleImplementation communicationModule;
    private final DecisionModuleImplementation decisionModule;

    private final Goal explore;
    private final Goal becomeWorker;

    private boolean isAtTestPosition = false;

    private int testCounter = 0;

    /**
     * Constructor
     *
     * @param name    the agent's name
     * @param mailbox the mail facility
     */
    public MovementTestAgent(String name, MailService mailbox) {
        super(name, mailbox);
        PerceptionAndMemoryLinker linker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = linker.getPerceptionAndMemory();
        this.communicationModule = new CommunicationModuleImplementation(name, mailbox);
        this.communicationModule.addSwarmSightController(linker.getSwarmSightController());
        this.decisionModule = new DecisionModuleImplementation(this.perceptionAndMemory, communicationModule,new Tounament4Config());
        this.explore = new G6GoalExploreV2(perceptionAndMemory);
        this.becomeWorker = new G6GoalChangeRole("worker", perceptionAndMemory);
    }

    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        G6Action action = null;
        if (perceptionAndMemory.getCurrentRole() == null) return new Skip();
        perceptionAndMemory.finishSync();
        if (perceptionAndMemory.isReadyForAction()) {
            if(perceptionAndMemory.getDispensers().isEmpty()
                    || !perceptionAndMemory.getCurrentRole().getName().equals("worker")){
                if (becomeWorker.preconditionsMet()){
                    say("Goal : become worker");
                    return (Action) becomeWorker.getNextAction();
                }
                say("Goal : explore");
                return (Action) explore.getNextAction();
            }
        }
        Block closestDispenser = perceptionAndMemory.getDispensers()
                .stream()
                .min(Comparator.comparingInt(x-> x.getCoordinates().manhattanDistanceTo(new Point(0,0))))
                .orElse(new Block(new Point (0,1), ""));

        if(closestDispenser.getCoordinates().equals(new Point(0,0))) isAtTestPosition = true;
        if(!isAtTestPosition){
            say("Goal : move to Test Position " + closestDispenser.getCoordinates());
            return (Action) AStar.astarNextStepWithAgents(closestDispenser.getCoordinates(), perceptionAndMemory).orElseThrow();
        }
        say("Goal : Running test No. " + testCounter);
        say("Current speed : " + perceptionAndMemory.getCurrentRole().getMovementSpeed().get(0));
        switch (testCounter){
            case 0 -> {
                //Test Standard Movement, 1 Direction, speed 2
                testCounter++;
                return new Move(Direction.EAST);
            }
            case 1 -> {
                //Test Standard Movement, 1 Direction, speed 2, back to base
                int speed = SpeedCalculator.determineSpeedOfLastAction(perceptionAndMemory.getLastAction(), perceptionAndMemory.getLastStepsMaxSpeed());
                if (speed != 2){
                    System.out.println("TestCounter : " + testCounter + ", " + "speed = " + speed);
                    throw new RuntimeException();
                }
                Movement lastMovement = new Movement(parameterlistToListOfDirections((ParameterList) perceptionAndMemory.getLastAction().getParameters().get(0)), speed);
                if (!lastMovement.asVector().equals(closestDispenser.getCoordinates().invert())){
                    System.out.println("TestCounter : " + testCounter + ", " + "lastMovement : " + lastMovement.asVector() + " expected : " + closestDispenser.getCoordinates().invert());
                    throw new RuntimeException();
                }
                testCounter ++;
                return new Move(Direction.WEST);
            }
            //Testcase for double in one direction
            case 2 -> {
                int speed = SpeedCalculator.determineSpeedOfLastAction(perceptionAndMemory.getLastAction(), perceptionAndMemory.getLastStepsMaxSpeed());
                if (speed != 2){
                    System.out.println("TestCounter : " + testCounter + ", " + "speed = " + speed);
                    throw new RuntimeException();
                }
                Movement lastMovement = new Movement(parameterlistToListOfDirections((ParameterList) perceptionAndMemory.getLastAction().getParameters().get(0)), speed);
                if (!closestDispenser.getCoordinates().equals(new Point(0,0))){
                    System.out.println("TestCounter : " + testCounter + ", " + "lastMovement : " + lastMovement.asVector() + " expected : " + closestDispenser.getCoordinates().invert());
                    throw new RuntimeException();
                }

                testCounter++;
                return new Move(new Direction[]{Direction.SOUTH, Direction.SOUTH});
            }
            //Testcase for double in one direction, back to base
            case 3 -> {
                int speed = SpeedCalculator.determineSpeedOfLastAction(perceptionAndMemory.getLastAction(), perceptionAndMemory.getLastStepsMaxSpeed());
                if (speed != 2){
                    System.out.println("TestCounter : " + testCounter + ", " + "speed = " + speed);
                    throw new RuntimeException();
                }
                Movement lastMovement = new Movement(parameterlistToListOfDirections((ParameterList) perceptionAndMemory.getLastAction().getParameters().get(0)), speed);
                if (!closestDispenser.getCoordinates().equals(lastMovement.asVector().invert())){
                    System.out.println("TestCounter : " + testCounter + ", " + "lastMovement : " + lastMovement.asVector() + " expected : " + closestDispenser.getCoordinates().invert());
                    throw new RuntimeException();
                }

                testCounter++;
                return new Move(Direction.NORTH, Direction.NORTH);
            }
            //Testcase move around the corner
            case 4 -> {
                int speed = SpeedCalculator.determineSpeedOfLastAction(perceptionAndMemory.getLastAction(), perceptionAndMemory.getLastStepsMaxSpeed());
                if (speed != 2){
                    System.out.println("TestCounter : " + testCounter + ", " + "speed = " + speed);
                    throw new RuntimeException();
                }
                Movement lastMovement = new Movement(parameterlistToListOfDirections((ParameterList) perceptionAndMemory.getLastAction().getParameters().get(0)), speed);
                if (!closestDispenser.getCoordinates().equals(new Point(0,0))){
                    System.out.println("TestCounter : " + testCounter + ", " + "lastMovement : " + lastMovement.asVector() + " expected : " + closestDispenser.getCoordinates().invert());
                    throw new RuntimeException();
                }

                testCounter++;
                return new Move(new Direction[]{Direction.WEST, Direction.SOUTH});
            }
            //Testcase move around the corner
            case 5 -> {
                int speed = SpeedCalculator.determineSpeedOfLastAction(perceptionAndMemory.getLastAction(), perceptionAndMemory.getLastStepsMaxSpeed());
                if (speed != 2){
                    System.out.println("TestCounter : " + testCounter + ", " + "speed = " + speed);
                    throw new RuntimeException();
                }
                Movement lastMovement = new Movement(parameterlistToListOfDirections((ParameterList) perceptionAndMemory.getLastAction().getParameters().get(0)), speed);
                if (!closestDispenser.getCoordinates().equals(lastMovement.asVector().invert())){
                    System.out.println("TestCounter : " + testCounter + ", " + "lastMovement : " + lastMovement.asVector() + " expected : " + closestDispenser.getCoordinates().invert());
                    throw new RuntimeException();
                }

                testCounter++;
                return new Move(Direction.WEST, Direction.SOUTH);
            }

            case 6  -> {throw new RuntimeException("ALL TESTS POSITIVE");}
        }


        return (eis.iilang.Action) action;
    }

    @Override
    public void handleMessage(Percept message, String sender) {

    }

    @Override
    public void handlePerceptionforStep() {
        perceptionAndMemory.handlePercepts(getPercepts());
    }

    @Override
    public void handleSyncRequests() {

    }

    @Override
    public void initialiseSync() {

    }

    private List<Direction> parameterlistToListOfDirections(@NotNull ParameterList directionIdentifiers) {
        List<Direction> directions = new ArrayList<>(directionIdentifiers.size());
        for (Parameter p : directionIdentifiers) {
            directions.add(Direction.fromIdentifier((Identifier) p));
        }
        return directions;
    }
}
