package g6Agent.communicationModule;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.communicationModule.submodules.PingCommunicator;
import g6Agent.communicationModule.submodules.StrategyModule;
import g6Agent.communicationModule.submodules.TaskAuctionModule;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleSwarmSightControllerInterface;

/**
 * Module to handle the communication between agents
 */
public interface CommunicationModule {

    /**
     * Handles the incoming Message
     * @param message the message
     * @param sender the sender
     */
    void handleMessage(Percept message, String sender);

    void addSwarmSightController(CommunicationModuleSwarmSightControllerInterface swarmSightController);

    /**
     * Broadcasts a "MOVEMENT_ATTEMPT" message, if the attempted action is one.
     *
     * @param action the action
     */
    void broadcastActionAttempt(Action action);

    /**
     * Returns the Module responsible for organizing and auctioning the tasks between the agents
     * @return the Module
     */
    TaskAuctionModule getTaskAuctionModule();

    /**
     * Returns the StrategyModule responsible for communicating the Strategies of the Agents
     * @return the Module
     */
    StrategyModule getStrategyModule();

    /**
     *
     * @return the PingCommunicator Module responsible for communicating pings between Agents
     */
    PingCommunicator getPingCommunicator();
}
