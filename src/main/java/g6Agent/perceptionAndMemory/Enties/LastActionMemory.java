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
}
