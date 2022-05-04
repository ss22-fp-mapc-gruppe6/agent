package g6Agent.perceptionAndMemory.AgentMap;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.agents.Agent;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InternalMapOfOtherAgents {
    private final PerceptionAndMemory perceptionAndMemory;
    private final MailService mailservice;
    private final Agent agent;
    private final HashMap<String, InternalMapEntry> relativePositionOfOtherAgents;

    public InternalMapOfOtherAgents(Agent agent, PerceptionAndMemory perceptionAndMemory, MailService mailservice) {
        this.agent = agent;
        this.perceptionAndMemory = perceptionAndMemory;
        this.mailservice = mailservice;
        this.relativePositionOfOtherAgents = new HashMap<>();
    }

    /**
     * Used if the Agent spots another Agent
     * @param agentname the agents name
     * @param position the agents position
     */
    public void spottetAgent(String agentname, Point position){
        updateAgent(agentname, new InternalMapEntry(position));
    }

    /**
     * Used if the Agent hears about the realtive Position of another Agent
     * @param agentname the agents name
     * @param relativePositionNotified the relative Position of the
     * @param notifyingAgentsName the Name of the Agent who notified of Agent Position
     *
     */
    public void heardOfAgentPosition(String agentname, InternalMapEntry relativePositionNotified, String notifyingAgentsName){
        //case the notifying Agent has
        if (!isKnown(notifyingAgentsName)){return;}
        InternalMapEntry positionOfNotifyingAgent = relativePositionOfOtherAgents.get(notifyingAgentsName);
        //case the internal position is more recent
        if(positionOfNotifyingAgent.getCounter() <= relativePositionNotified.getCounter()){
            return;
        }
        //Computing the relative position from self
        int xPos = relativePositionNotified.getPosition().x + positionOfNotifyingAgent.getPosition().x;
        int yPos = relativePositionNotified.getPosition().y + positionOfNotifyingAgent.getPosition().y;
        InternalMapEntry entry = new InternalMapEntry(new Point(xPos,yPos), relativePositionNotified.getCounter());
        updateAgent(agentname, entry);
    }

    /**
     * Updates the Position of an Agent, if the lastSeenCounter is more recent.
     * Or Registers the Agent, if it is not known yet.
     *
     * @param agentname
     * @param entry
     */
    protected void updateAgent(String agentname, InternalMapEntry entry) {
        //Case doesnt have a entry yet.
        if (!isKnown(agentname)) {
            relativePositionOfOtherAgents.put(agentname, entry);
            return;
        }
        //Case new lastSeenCounter is more recent
        if (relativePositionOfOtherAgents.get(agentname).getCounter() > entry.getCounter()) {
            relativePositionOfOtherAgents.replace(agentname, entry);
            return;
        }
    }

    public InternalMapEntry getAgentPosition(String agentname) {
        return this.relativePositionOfOtherAgents.get(agentname);
    }

    /**
     * Increments all lastSeenCounters
     */
    public void incrementAllCounters() {
         relativePositionOfOtherAgents.forEach(
                 (key, entry) -> entry.increaseCounter()
         );
    }

    /**
     * returns if the Agent with the According Agentname is known.
     * @param agentname the agents name
     * @return does he have an entry?
     */
    public boolean isKnown(String agentname){
        return relativePositionOfOtherAgents.get(agentname) != null;
    }

    /**
     *  Updates all entries according to the movement of the Agent owning the internal Representation.
     * @param movement the movement
     */
    private void movedMyself(Movement movement) {
     movedMyself(movement.direction(), movement.speed());
    }
    /**
     * Updates all entries according to the movement of the Agent owning the internal Representation.
      * @param direction the direction the Agent was moving.
     * @param speed the number of fields the Agent was moving
     */
    public void movedMyself(Direction direction, int speed){
        updateAllEntries(direction.getNextCoordinate().invert().multiply(speed));
    }

    public void notifiedOfMovement(String agentname, Direction direction, int speed){
        if(isKnown(agentname)){
            updateSingleEntry(agentname, direction.getNextCoordinate().multiply(speed));
        }
    }

    private void updateSingleEntry(String agentname, Point offset) {
        InternalMapEntry agentPos = relativePositionOfOtherAgents.get(agentname);
        int xPos = agentPos.getPosition().x + offset.x;
        int yPos = agentPos.getPosition().y + offset.y;
        agentPos.setPosition(new Point(xPos,yPos));
    }

    private void updateAllEntries(Point offset){
        relativePositionOfOtherAgents.forEach(
                (key, entry) -> {
                    int xPos = entry.getPosition().x + offset.x;
                    int yPos = entry.getPosition().y + offset.y;
                    entry.setPosition(new Point(xPos, yPos));
                }
        );
    }

    public void checkAndNotifyIfMovedLastStep() {
        LastActionMemory lastAction = perceptionAndMemory.getLastAction();
        //check if agent moved
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
                movedMyself(movement);
                mailservice.broadcast(new Percept("MOVEMENT_NOTIFICATION", movement.asParameterList()), agent.getName());
            }
        }
    }




}
