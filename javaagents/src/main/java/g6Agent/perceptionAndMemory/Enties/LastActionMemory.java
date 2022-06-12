package g6Agent.perceptionAndMemory.Enties;

import eis.iilang.Parameter;

import java.util.ArrayList;
import java.util.List;


/**
 * Class to encapsulate the Information of the Last Action, coming from the Server
 */
public class LastActionMemory {
    private List<Parameter> lastActionParameters;
    private String name;
    private String lastActionSuccessfulMessage;

    public LastActionMemory() {
        this.lastActionParameters = new ArrayList<>();
        this.name = "";
        this.lastActionSuccessfulMessage = "";
    }

    /**
     * @return the Parameters of the last Action
     */
    public List<Parameter> getParameters() {
        return lastActionParameters;
    }

    /**
     * @param lastActionParameters the Parameters of the last Action
     */
    public void setLastActionParameters(List<Parameter> lastActionParameters) {
        this.lastActionParameters = lastActionParameters;
    }

    /**
     *
     * @return the name of the last action
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name of the last action
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <pre>
     * Message that gives the success or error message of the last action.
     * If it was successful it says "success"
     * If not the failure code it is action dependent:
     *
     * Action move :
     * failed_parameter  - 	No parameters given or at least one parameter is not a valid direction.
     * failed_path       -	The first move was blocked.
     * partial_success   -	At least the first step worked but one of the later moves was blocked.
     *
     * Action attach :
     * failed_parameter  -	Parameter is not a direction.
     * failed_target     -	There is nothing to attach in the given direction.
     * failed_blocked    - 	The thing is already attached to an opponent agent.
     * failed 	         -  The agent already has too many things attached.
     *
     *
     * Action detach:
     * failed_parameter  -	Parameter is not a direction.
     * failed_target     - 	There was no attachment to detach in the given direction.
     * failed 	         -  There was a thing but not attached to the agent.
     *
     *
     * Action rotate:
     *
     * failed_parameter  -	 Parameter is not a (rotation) direction.
     * failed 	         -   One of the things attached to the agent cannot rotate to its target position OR the agent is currently attached to another agent.
     *
     *
     * Action connected:
     * failed_parameter -   First parameter is not an agent of the same team OR x and y cannot be parsed to valid integers.
     * failed_partner 	-   The partner's action is not connect OR failed randomly OR has wrong parameters.
     * failed_target 	-   At least one of the specified blocks is not at the given position or not attached to the agent or already attached to the other agent.
     * failed 	        -   The given positions are too far apart OR one agent is already attached to the other (or through other blocks), or connecting both blocks would violate the size limit for connected structures.
     *
     * Action disconnect:
     * failed_parameter -	No valid integer coordinates given.
     * failed_target 	-   Target locations aren't attachments of the agent or not attached to each other directly.
     *
     * Action request:
     * failed_parameter  -	Parameter is not a direction.
     * failed_target 	 -  No dispenser was found in the specific position.
     * failed_blocked 	 -  The dispenser's position is currently blocked by another agent or thing.
     *
     * Action submit:
     * failed_target    -	No active task could be associated with first parameter, or task has not been accepted by the agent.
     * failed 	        -   One or more of the requested blocks are missing OR the agent is not on a goal terrain.
     *
     * Action clear:
     * failed_parameter  -	No valid integer coordinates given.
     * failed_target 	 -  Target location is not within the agent's vision radius or outside the grid.
     * failed_resources  - 	The agent's energy is too low.
     * failed_location 	 -  The agent is targeting a cell out of reach.
     * failed_random 	 -  The action failed due to random failure or the additional probability to fail.
     *
     * Action adopt:
     * failed_parameter  -	No parameter or parameter is not a valid role name.
     * failed_location 	 -  Agent is not in a role zone.
     *
     * Action survey:
     * failed_parameter  - 	Parameters are not coordinates or too many parameters given.
     * failed_location 	 -  The location is outside the agent's vision.
     * failed_target 	 -  There is no entity at the given location. It might have moved away before.
     *
     * Action all actions:
     * failed_random 	-   The action failed randomly.
     * failed_status 	-   The agent is deactivated.
     * failed_role 	    -   The agent's current role does not permit the action.
     * unknown_action 	-   The action is not part of the game.
     * </pre>
     * @return the message
     */
    public String getSuccessMessage() {
        return lastActionSuccessfulMessage;
    }

    /**
     * @param lastActionSuccessful was the last action successfull?
     */
    public void setSuccessfulMessage(String lastActionSuccessful) {
        lastActionSuccessfulMessage = lastActionSuccessful;
    }

    @Override
    public String toString(){
        return "Name : " + this.getName() +"\n" +
                "Success Message : " + this.getSuccessMessage() + "\n" +
                "Parameters : " + this.getParameters();
    }
}
