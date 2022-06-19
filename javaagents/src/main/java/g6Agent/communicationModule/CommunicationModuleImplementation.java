package g6Agent.communicationModule;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleSwarmSightControllerInterface;

import java.util.ArrayList;
import java.util.List;

public class CommunicationModuleImplementation implements CommunicationModule{
    List<CommunicationModuleSwarmSightControllerInterface> swarmSightControllers;

    public CommunicationModuleImplementation() {
        this.swarmSightControllers = new ArrayList<>();
    }

    @Override
    public void handleMessage(Percept message, String sender) {
        switch (message.getName()){
            case "MOVEMENT_NOTIFICATION"  -> {
                for (var agentMapCoordinator : swarmSightControllers) {
                    agentMapCoordinator.processMovementNotification(message, sender);
                }
            }
            case "MOVEMENT_ATTEMPT"  -> {
                for (var agentMapCoordinator : swarmSightControllers) {
                    agentMapCoordinator.processMovementAttempt(message, sender);
                }
            }
            case "INTRODUCTION_REQUEST" -> {
                for (var agentMapCoordinator : swarmSightControllers) {
                    agentMapCoordinator.processIntroductionRequest(message, sender);
                }
            }
            case "INTRODUCTION_ACCEPT" -> {
                for (var agentMapCoordinator : swarmSightControllers) {
                    agentMapCoordinator.processIntroductionAccept(message, sender);
                }
            }
            case "MY_VISION" -> {
                for (var agentMapCoordinator : swarmSightControllers) {
                    agentMapCoordinator.processVisionNotification(message, sender);
                }
            }
            case "KNOWN_AGENTS" -> {
                for (var agentMapCoordinator : swarmSightControllers) {
                    agentMapCoordinator.processKnownAgentsNotification(message, sender);
                }
            }
        }
    }

    @Override
    public void addSwarmSightController(CommunicationModuleSwarmSightControllerInterface swarmSightController) {
        swarmSightControllers.add(swarmSightController);
    }

    @Override
    public void broadcastActionAttempt(Action action) {
        if (action != null && action.getName().equals("move")) {
            for (CommunicationModuleSwarmSightControllerInterface agentMapCoordinator : swarmSightControllers) {
                agentMapCoordinator.broadcastActionAttempt(action);
            }
        }
    }
}
