/**
 * Interface for the {@link g6Agent.communicationModule.CommunicationModule} to
 * access the {@link g6Agent.perceptionAndMemory.SwarmSightController}
 *
 * @author Kai Müller
 */

package g6Agent.perceptionAndMemory.Interfaces;

import eis.iilang.Percept;

public interface CommunicationModuleSwarmSightControllerInterface {
    /**
     * Processes a message that notifies of the succsessfull movement of another Agent
     *
     * @param message the message
     * @param sender  the sender
     */
    void processMovementNotification(Percept message, String sender);

    /**
     * Processes a message, that notifies of what an Agent sees
     *
     * @param message the message
     * @param sender  the sender
     */
    void processVisionNotification(Percept message, String sender);

    /**
     * Processes an message, which contains the Agents known to the Sender
     *
     * @param message the message
     * @param sender  the sender
     */
    void processKnownAgentsNotification(Percept message, String sender);

    /**
     * decifers an "INTRODUCTION_REQUEST" message
     *
     * @param message the message
     * @param sender  the sender
     */
    void processIntroductionRequest(Percept message, String sender);

    /**
     * decifers and processes an "INTRODUCTION_ACCEPT" message
     * @param message the message
     * @param sender   the sender
     */
    void processIntroductionAccept(Percept message, String sender);
}
