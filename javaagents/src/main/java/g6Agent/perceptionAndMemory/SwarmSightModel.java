package g6Agent.perceptionAndMemory;


import g6Agent.perceptionAndMemory.Enties.AgentNameAndPosition;
import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class to save and track the Positions of other agents
 */
class SwarmSightModel {

    private final HashMap<String, InternalMapEntry> relativePositionOfOtherAgents;

    public SwarmSightModel() {
        this.relativePositionOfOtherAgents = new HashMap<>();
    }

    /**
     * Updates the entry of the sender with the given movement
     * @param sender the sender
     * @param movement the movement
     */
    public void notifiedOfMovement(String sender, Movement movement) {
        InternalMapEntry entry = relativePositionOfOtherAgents.get(sender);
        if (entry != null) {
            Point nextPositon = entry.getPosition().add(movement.asVector());
            entry.setPosition(nextPositon);
            InternalMapEntry updatedEntry = new InternalMapEntry(nextPositon, entry.getCounter());
            relativePositionOfOtherAgents.put(sender, updatedEntry);
        }
    }

    /**
     * Used if the Agent spots another Agent
     * @param agentname the agents name
     * @param position the agents position
     */
    public void spottetAgent(String agentname, Point position){
        this.relativePositionOfOtherAgents.put(agentname, new InternalMapEntry(position));
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
        relativePositionOfOtherAgents.put(agentname, entry);
    }


    public InternalMapEntry getAgentPosition(String agentname) {
        return this.relativePositionOfOtherAgents.get(agentname);
    }

    /**
     * Increments all lastSeenCounters
     */
    public void incrementAllCounters() {
         relativePositionOfOtherAgents.forEach(
                 (key, entry) -> {if(entry != null) {entry.increaseCounter();}}
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
    void movedMyself(Movement movement) {
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


    private void updateAllEntries(Point offset){
        relativePositionOfOtherAgents.forEach(
                (key, entry) -> {
                    int xPos = entry.getPosition().x + offset.x;
                    int yPos = entry.getPosition().y + offset.y;
                    entry.setPosition(new Point(xPos, yPos));
                }
        );
    }

    public List<AgentNameAndPosition> knownAgents() {
        List<AgentNameAndPosition> knownAgents = new ArrayList<>();
        relativePositionOfOtherAgents.forEach((key, entry) -> {
            if (entry != null) knownAgents.add(new AgentNameAndPosition(key, entry.getPosition()));
        });

        return knownAgents;
    }
}
