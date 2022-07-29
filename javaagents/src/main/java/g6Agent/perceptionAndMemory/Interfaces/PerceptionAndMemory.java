package g6Agent.perceptionAndMemory.Interfaces;

import eis.iilang.Percept;
import g6Agent.perceptionAndMemory.Enties.*;
import g6Agent.services.Point;


import java.util.AbstractMap;
import java.util.List;


/**
 * Module that implements the perception and memory. It is recommended, to use handlePercepts() in the beginning of the step cycle of an Agent.
 *
 * @author Kai Müller
 */

public interface PerceptionAndMemory {

    /**
     * Handles the Percepts of this step.
     *
     * @param perceptInput the Percepts recieved this step.
     */
    void handlePercepts(List<Percept> perceptInput);

    /**
     * Check for other Agents and send IntroductionRequests to unknown ones. Must be used after HandlePercepts()
     */
    void initiateSync();
    /**
     * Count introduction Requests send in initiateSwarmSightSync()
     */
    void handleSyncRequests();

    /**
     * finishes the sync process after handleSyncReuests()
     */
    void finishSync();

    /**
     * Returns a List of relative Positions of Obstacles
     *
     * @return the relative Positions.
     */
    List<Point> getObstacles();

    /**
     * @return the Name of the Agent, Null if it doesn't know it yet.
     */
    String getName();

    /**
     * @return the current Energy of the Agent
     */
    int getEnergy();

    /**
     * @return the current score of the Team
     */
    int getScore();

    /**
     * @return the Name of the Agents Team
     */
    String getTeam();

    /**
     * @return the Last Action of this Agent, if it was Succesfull and its Parameters
     */
    LastActionMemory getLastAction();


    /**
     * @return ist the Agent deactivated?
     */
    boolean isDeactivated();

    /**
     * Determines if the Agent received an new Action ID. And is not deactivated Resets to false with clearShortTermMemory()
     *
     * @return is the Agent ready for Action?
     */
    boolean isReadyForAction();

    /**
     * @return a List with Coordinates with Agents of the same Team, perceived this step
     */
    List<Point> getFriendlyAgents();


    /**
     * @return a List with Coordinates with Agents of the opposing Team, perceived this step
     */
    List<Point> getEnemyAgents();

    /**
     * @return the active Tasks known to Agent
     */
    List<Task> getActiveTasks();

    /**
     * @return the Tasks known to Agent, even if they are timed out
     */
    List<Task> getAllTasks();

    /**
     * @return the Blocks in Sight
     */
    List<Block> getBlocks();

    /**
     * @return the Dispensers in sight
     */
    List<Block> getDispensers();

    /**
     * @return the percieved markers
     */
    List<Marker> getMarkers();

    /**
     * @return the known Role Zones
     */
    List<Point> getRoleZones();

    /**
     * @return the known Goal Zones
     */
    List<Point> getGoalZones();

    int getSteps();

    /**
     * @return the step the agent is currently in
     */
    int getCurrentStep();

    /**
     * @return A List of Roles the Agent can take.
     */
    List<Role> getPossibleRoles();

    /**
     * @return the current Role of the Agent.
     */
    Role getCurrentRole();

    /**
     *
     * @return the Role the Agent had Last step
     */
    Role getLastStepsRole();

    /**
     * @return the teamsize of the Agents Team
     */
    int getTeamSize();

    //List<Point> getAttached();  @return positions at which something is attached to the Agent

    /**
     * Returns he blocks attached to all Agents in sight. including himself.
     * If you only wan't his own attached Blocks use getDirectlyAttachedBlocks()
     *
     * @return the blocks attached to all Agents in sight. including himself;
     */
    List<Block> getAttachedBlocks();

    /**
     * @return the blocks attached to this agent.
     */
    List<Block> getDirectlyAttachedBlocks();

    /**
     * @return the norms
     */
    List<Norm> getNorms();

    /**
     * Adds a listener for Last Actions
     *
     * @param listener the listener
     */
    void addLastActionListener(LastActionListener listener);

    /**
     * @return the Names and positions of the known agents.
     */
    List<AgentNameAndPosition> getKnownAgents();

    /**
     *
     * @return the position of the agent, null if unknown
     */
    Point getPositionOfKnownAgent(String agentname);

    /**
     * @return the current violation. Empty String if none this step.
     */
    String getViolation();
    /**
     *
     * @return the currently saved values in agent brain module
     */
    AbstractMap<Integer, Object> getBrainData();

    Integer getLastId();

}
