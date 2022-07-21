package g6Agent.perceptionAndMemory.Interfaces;

import g6Agent.perceptionAndMemory.Enties.AgentNameAndPosition;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Vision;
import g6Agent.services.Point;

import java.util.List;

/**
 * Interface to syncronize the vision of this agent with the vision of other Agents
 *
 * @author Kai Müller
 */
public interface AgentVisionReporter {

    /**
     * report what the vision of this Agent to other Agents
     * @param vision the Vision
     */
    void reportMyVision(Vision vision);

    /**
     * updates the Vision
     */
    void updateMyVisionWithSightingsOfOtherAgents();

    /**
     * use once per step, does its routine for this step
     */
    void handleStep();

    /**
     *
     * @return the Positions and Names of the Known Agents
     */
    List<AgentNameAndPosition> getKnownAgentPositions();

    /**
     * Returns the Position of an Agent with a given Name, null if is unknown
     * @param agentname the Name of the Agent
     * @return the position, null if is unknown
     */
    Point getPositionOfAgent(String agentname);
}
