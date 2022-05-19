package g6Agent.agents;

import eis.iilang.*;
import g6Agent.actions.*;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;


public class MyTestAgent extends Agent{

    private final PerceptionAndMemory perceptionAndMemory;
    //public HashMap<Integer, AgentStep> stepValues = new HashMap<>();
    //private GridObject grid;


    public MyTestAgent(String name, MailService mailbox){
        super(name, mailbox);
        this.perceptionAndMemory = new PerceptionAndMemoryImplementation();
    }

/*
    public Point getPosition(int step) {
        return stepValues.get(step).getAgentMapPos();
    }

    public GridObject getGrid() {
        return (grid);
    }
*/
    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        Action action = null;
        perceptionAndMemory.handlePercepts(getPercepts());
        if (perceptionAndMemory.isReadyForAction()) {
            //skip - works
            //action = new Skip();

            //move - works
            action = new Move(Direction.WEST);

            //rotate - works
            if (perceptionAndMemory.getCurrentStep() % 2 == 1) {
                action = new Rotate(Rotation.CLOCKWISE);
            }

            //clear - works
            for (Point obstacle : perceptionAndMemory.getObstacles()) {
                if (obstacle.isAdjacent() && perceptionAndMemory.getEnergy() > 19) {
                    action = new Clear(obstacle);
                }
            }


            if (perceptionAndMemory.getCurrentRole() != null && perceptionAndMemory.getCurrentRole().getName().equals("worker")) {
                //request - works
                for (Block dispenser : perceptionAndMemory.getDispensers()) {
                    if (dispenser.getCoordinates().isAdjacent()) {
                        for (Direction direction : Direction.allDirections()) {
                            if (direction.getNextCoordinate().equals(dispenser.getCoordinates())) {
                                action = new Request(direction);
                            }
                        }
                    }
                }
                //attach - works
                for (Block block : perceptionAndMemory.getBlocks()) {
                    if (block.getCoordinates().isAdjacent()) {
                        for (Direction direction : Direction.allDirections()) {
                            if (direction.getNextCoordinate().equals(block.getCoordinates())) {
                                action = new Attach(direction);
                            }
                        }
                    }
                }
            }
            //detach - works
            if (perceptionAndMemory.getAttached().size() > 0) {
                say("I'VE GOT A BLOCK, MOTHERFUCKERS!");
                for (Direction direction : Direction.allDirections()) {
                    Point attached = perceptionAndMemory.getAttached().get(0);
                    if (direction.getNextCoordinate().equals(attached)) {
                        action = new Detach(direction);
                    }
                }
            }
            //adopt - works
            if (perceptionAndMemory.getCurrentRole() != null && !perceptionAndMemory.getCurrentRole().getName().equals("worker")) {
                for (Point roleZone : perceptionAndMemory.getRoleZones()) {
                    if (roleZone.equals(new Point(0, 0))) {
                        action = new Adopt("worker");
                    }
                }
            }


            //say(perceptionAndMemory.getLastAction().getName());
            //say(perceptionAndMemory.getLastAction().getSuccessMessage());


            //System.out.println(perceptionAndMemory.getCurrentRole().getName());
            //System.out.println(perceptionAndMemory.getCurrentRole().getPossibleActions());
            if (perceptionAndMemory.getLastAction().getName().equals("request") && perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")){
                say("successfully requested block");
            }
            if (perceptionAndMemory.getLastAction().getName().equals("detach") && perceptionAndMemory.getLastAction().getSuccessMessage().equals("success")){
                say("successfully detached block");
            }
        }
        return action;
    }



    @Override
    public void handleMessage(Percept message, String sender) {

    }
/*
    public void updateGridPosition(Point vector, int step) {
        stepValues.get(step).getAgentMapPos().add(vector);

    }
*/

}
