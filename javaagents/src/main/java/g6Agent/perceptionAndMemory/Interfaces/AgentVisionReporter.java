package g6Agent.perceptionAndMemory.Interfaces;

import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.services.Point;

import java.util.List;

/**
 * NOT IMPLEMENTED!
 *
 * Interface to syncronize the vision of this agent with the vision of other Agents
 *
 */
public interface AgentVisionReporter {

    void reportMyVision(List<Block> dispensers, List<Block> blocks, List<Point> roleZones, List<Point> goalZones, List<Point> obstacles);

    void updateMyVisionWithSightingsOfOtherAgents();

    void handleStep();
}
