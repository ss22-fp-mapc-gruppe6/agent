package g6Agent.perceptionAndMemory.Interfaces;

import eis.iilang.Percept;

public interface AgentAgentMapCoordinaterInterface {
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
    void processVisionNotificationNotification(Percept message, String sender);
}
