package g6Agent.communicationModule;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleAgentMapCoordinatorInterface;

import java.util.ArrayList;
import java.util.List;

public class CommunicationModuleImplementation implements CommunicationModule{
    List<CommunicationModuleAgentMapCoordinatorInterface> agentMapCordinators;

    public CommunicationModuleImplementation() {
        this.agentMapCordinators = new ArrayList<>();
    }

    @Override
    public void handleMessage(Percept message, String sender) {
        switch (message.getName()){
            case "MOVEMENT_NOTIFICATION"  -> {
                for (CommunicationModuleAgentMapCoordinatorInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processMovementNotification(message, sender);
                }
            }
            case "MOVEMENT_ATTEMPT"  -> {
                for (CommunicationModuleAgentMapCoordinatorInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processActionAttempt(message, sender);
                }
            }
            case "INTRODUCTION_REQUEST" -> {
                for (CommunicationModuleAgentMapCoordinatorInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processIntroductionRequest(message, sender);
                }
            }
            case "INTRODUCTION_ACCEPT" -> {
                for (CommunicationModuleAgentMapCoordinatorInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processIntroductionAccept(message, sender);
                }
            }
            case "MY_VISION" -> {
                for (CommunicationModuleAgentMapCoordinatorInterface agentMapCoordinator : agentMapCordinators) {
                    agentMapCoordinator.processVisionNotification(message, sender);
                }
            }
        }
    }

    @Override
    public void addAgentMapCoordinator(CommunicationModuleAgentMapCoordinatorInterface agentmapcoordinator) {
        agentMapCordinators.add(agentmapcoordinator);
    }

    @Override
    public void broadcastActionAttempt(Action action) {
        if (action.getName().equals("move")) {
            for (CommunicationModuleAgentMapCoordinatorInterface agentMapCoordinator : agentMapCordinators) {
                agentMapCoordinator.broadcastActionAttempt(action);
            }
        }
    }
}
