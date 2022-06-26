package g6Agent.communicationModule;

import eis.iilang.*;
import g6Agent.MailService;

import g6Agent.communicationModule.submodules.StrategyModule;
import g6Agent.communicationModule.submodules.TaskAuctionModule;
import g6Agent.perceptionAndMemory.Interfaces.CommunicationModuleSwarmSightControllerInterface;

import java.util.ArrayList;
import java.util.List;

public class CommunicationModuleImplementation implements CommunicationModule{
    private final String agentname;
    private final MailService mailService;
    private final List<CommunicationModuleSwarmSightControllerInterface> swarmSightControllers;

    private final TaskAuctionModule taskAuctionModule;

    private final StrategyModule strategyModule;

    public CommunicationModuleImplementation(String agentname, MailService mailService) {
        this.agentname = agentname;
        this.mailService = mailService;
        this.swarmSightControllers = new ArrayList<>();
        this.taskAuctionModule = new TaskAuctionModule(agentname, mailService);
        this.strategyModule = new StrategyModule(agentname, mailService);
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
            case "MY_TASK" -> taskAuctionModule.receiveTaskAndBlockIndex(message, sender);
            case "MY_STRATEGY" -> strategyModule.receiveStrategyUpdate(message, sender);
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

    @Override
    public TaskAuctionModule getTaskAuctionModule() {
        return this.taskAuctionModule;
    }

    @Override
    public StrategyModule getStrategyModule() {
        return this.strategyModule;
    }


}
