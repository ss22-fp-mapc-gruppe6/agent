package g6Agent.decisionModule.submodules;

import g6Agent.communicationModule.submodules.TaskAuctionModule;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;


/**
 * A Filter to let only those agents which work at the same task receive pings,
 * also responsible for recalibrating the coordinates to the senders position
 */
public class TaskPingFilter implements PingFilter{

    private final TaskAuctionModule taskAuctionModule;
    private final PerceptionAndMemory perceptionAndMemory;

    public TaskPingFilter(TaskAuctionModule taskAuctionModule, PerceptionAndMemory perceptionAndMemory) {
        this.taskAuctionModule = taskAuctionModule;
        this.perceptionAndMemory = perceptionAndMemory;
    }


    @Override
    public boolean pingAccepted(Point point, String sender) {
        return taskAuctionModule
                .getAgentsAtSameTask()
                .stream()
                .anyMatch(agent -> agent.equals(sender));
    }

    @Override
    public Point recalculatePingPosition(Point point, String sender) {
            Point relativePositionOfSender =  perceptionAndMemory.getPositionOfKnownAgent(sender);
            if (relativePositionOfSender== null) return null;
            return point.add(relativePositionOfSender);
    }
}
