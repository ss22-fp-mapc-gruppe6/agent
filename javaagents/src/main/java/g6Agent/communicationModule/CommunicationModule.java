package g6Agent.communicationModule;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleSwarmSightControllerInterface;

public interface CommunicationModule {

    void handleMessage(Percept message, String sender);

    void addSwarmSightController(CommunicationModuleSwarmSightControllerInterface swarmSightController);
    /**
     * Broadcasts a "MOVEMENT_ATTEMPT" message, if the attempted action is one.
     * @param action the action
     */
    void broadcastActionAttempt(Action action);
}
