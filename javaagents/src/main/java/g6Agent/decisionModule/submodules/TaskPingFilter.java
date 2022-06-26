package g6Agent.decisionModule.submodules;

import g6Agent.communicationModule.submodules.TaskAuctionModule;
import g6Agent.services.Point;


/**
 * A Filter to let only those agents which work at the same task receive pings
 */
public class TaskPingFilter implements PingFilter{

    private final TaskAuctionModule taskAuctionModule;

    public TaskPingFilter(TaskAuctionModule taskAuctionModule) {
        this.taskAuctionModule = taskAuctionModule;
    }


    @Override
    public boolean pingAccepted(Point point, String sender) {
        return taskAuctionModule
                .getAgentsAtSameTask()
                .stream()
                .anyMatch(agent -> agent.equals(sender));
    }
}
