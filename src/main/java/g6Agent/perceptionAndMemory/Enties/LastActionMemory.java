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
    private boolean isLastActionSuccessful;

    public LastActionMemory() {
        this.lastActionParameters = new ArrayList<>();
        this.name = "lastAction";
        this.isLastActionSuccessful = false;
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
     *
     * @return was the last action successful?
     */
    public boolean wasSuccessful() {
        return isLastActionSuccessful;
    }

    /**
     * @param lastActionSuccessful was the last action successfull?
     */
    public void setWasSuccessful(boolean lastActionSuccessful) {
        isLastActionSuccessful = lastActionSuccessful;
    }
}
