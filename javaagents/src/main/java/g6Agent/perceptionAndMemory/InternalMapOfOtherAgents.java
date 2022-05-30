package g6Agent.perceptionAndMemory;


import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class InternalMapOfOtherAgents {
    private final String agentname;
    private final HashMap<String, InternalMapEntry> relativePositionOfOtherAgents;

    public InternalMapOfOtherAgents(String agentname) {
        this.agentname = agentname;
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
     * @param agentname the agents name
     * @param entry the entry
     */
    protected void updateAgent(String agentname, InternalMapEntry entry) {
        //Case doesnt have a entry yet.
        if (!isKnown(agentname)) {
            relativePositionOfOtherAgents.put(agentname, entry);
            return;
        }
        //Case new lastSeenCounter is more recent
       // if (relativePositionOfOtherAgents.get(agentname).getCounter() > entry.getCounter()) {
            relativePositionOfOtherAgents.put(agentname, entry);
        //}
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
