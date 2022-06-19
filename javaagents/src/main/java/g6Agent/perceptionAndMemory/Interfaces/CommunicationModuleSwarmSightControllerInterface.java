package g6Agent.perceptionAndMemory.Interfaces;

import eis.iilang.Action;
import eis.iilang.Percept;

public interface CommunicationModuleSwarmSightControllerInterface {
    /**
     * Processes a message that notifies of the succsessfull movement of another Agent
     * @param message the message
     * @param sender   the sender
     */
    void processMovementNotification(Percept message, String sender);

    /**
     * Processes a message, that notifies of what an Agent sees
     * @param message the message
     * @param sender   the sender
     */
    void processVisionNotification(Percept message, String sender);

    /**
     * Processes an message, which contains the Agents known to the Sender
     * @param message the message
     * @param sender  the sender
     */
    void processKnownAgentsNotification(Percept message, String sender);

    /**
     * Broadcasts a "MOVEMENT_ATTEMPT" message, if the attempted action is one.
     * @param action the action
     */
    void broadcastActionAttempt(Action action);

    /**
     * decifers an "ACTION_ATTEMPT" message
     * @param message the message
     * @param sender the sender
     */
    void processMovementAttempt(Percept message, String sender);

    /**
     * decifers an "INTRODUCTION_REQUEST" message
     * @param message the message
     * @param sender the sender
     */
    void processIntroductionRequest(Percept message, String sender);


    void processIntroductionAccept(Percept message, String sender);
}
