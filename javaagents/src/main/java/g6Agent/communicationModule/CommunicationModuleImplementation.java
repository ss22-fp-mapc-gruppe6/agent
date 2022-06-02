package g6Agent.communicationModule;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleSwarmSightControllerInterface;

import java.util.ArrayList;
import java.util.List;

public class CommunicationModuleImplementation implements CommunicationModule{
    List<CommunicationModuleSwarmSightControllerInterface> agentMapCordinators;

    public CommunicationModuleImplementation() {
        this.agentMapCordinators = new ArrayList<>();
    }

    @Override
    public void handleMessage(Percept message, String sender) {
        switch (message.getName()){
            case "MOVEMENT_NOTIFICATION"  -> {
                for (CommunicationModuleSwarmSightControllerInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processMovementNotification(message, sender);
                }
            }
            case "MOVEMENT_ATTEMPT"  -> {
                for (CommunicationModuleSwarmSightControllerInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processMovementAttempt(message, sender);
                }
            }
            case "INTRODUCTION_REQUEST" -> {
                for (CommunicationModuleSwarmSightControllerInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processIntroductionRequest(message, sender);
                }
            }
            case "INTRODUCTION_ACCEPT" -> {
                for (CommunicationModuleSwarmSightControllerInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processIntroductionAccept(message, sender);
                }
            }
            case "MY_VISION" -> {
                for (CommunicationModuleSwarmSightControllerInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processVisionNotification(message, sender);
                }
            }
        }
    }

    @Override
    public void addAgentMapCoordinator(CommunicationModuleSwarmSightControllerInterface agentmapcoordinator) {
        agentMapCordinators.add(agentmapcoordinator);
    }

    @Override
    public void broadcastActionAttempt(Action action) {
        if (action != null && action.getName().equals("move")) {
            for (CommunicationModuleSwarmSightControllerInterface agentMapCoordinator : agentMapCordinators) {
                agentMapCoordinator.broadcastActionAttempt(action);
            }
        }
    }
}
