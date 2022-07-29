package g6Agent.decisionModule;

import g6Agent.communicationModule.CommunicationModule;
import g6Agent.communicationModule.entities.AuctionModuleEntry;
import g6Agent.communicationModule.entities.SubTaskWithCost;
import g6Agent.communicationModule.submodules.PingCommunicator;
import g6Agent.communicationModule.submodules.StrategyModule;
import g6Agent.communicationModule.submodules.TaskAuctionModule;
import g6Agent.decisionModule.configurations.DecisionModuleConfiguration;
import g6Agent.decisionModule.entities.Strategy;
import g6Agent.communicationModule.submodules.TaskPingFilter;
import g6Agent.goals.*;
import g6Agent.goals.interfaces.GoalWithPriorityOffset;
import g6Agent.goals.interfaces.PingReceiver;
import g6Agent.goals.old.G6GoalExplore;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

/**
 * Module to generate and decide about the current Goal of an Agent
 */

public class DecisionModuleImplementation implements DecisionModule {
    private final PerceptionAndMemory perceptionAndMemory;
    private final CommunicationModule communicationModule;
    private final TaskAuctionModule taskAuctionModule; //responsible for auctoning the tasks and subtasks (the single Blocks in requirements)
    private final StrategyModule strategyModule;
    private Strategy strategy; //can be switched to determine the current behavior of the Agent (OFFENSE or DEFENCE )

    private final PingCommunicator pingCommunicator;
    private final DecisionModuleConfiguration configuration; //handles the Goal and strategy configuration

    private Goal currentGoal;

    /**
     * Construktor
     * @param perceptionAndMemory the beliefs of the agent
     * @param communicationModule the communication module
     * @param configuration the configuration of the DecisionModule
     */
    public DecisionModuleImplementation(PerceptionAndMemory perceptionAndMemory, CommunicationModule communicationModule, DecisionModuleConfiguration configuration) {
        this.perceptionAndMemory = perceptionAndMemory;
        this.communicationModule = communicationModule;
        this.taskAuctionModule = communicationModule.getTaskAuctionModule();
        this.strategyModule = communicationModule.getStrategyModule();
        this.strategy = Strategy.OFFENSE;
        this.currentGoal = new G6GoalExplore(perceptionAndMemory);
        this.pingCommunicator = communicationModule.getPingCommunicator(); // responsible for sending pings
        pingCommunicator.addPingFilter(new TaskPingFilter(taskAuctionModule, perceptionAndMemory)); //add filter, so only agents who work at the same task receive a ping
        this.configuration = configuration; //Sets the Goals and Strategy
    }

    @Override
    public Goal revalidateGoal() {
        revalidateStrategy();
        List<Goal> goal_options = generateGoals();
        //filter Options and use only those, which preconditions are met
        goal_options = goal_options
                .stream()
                .filter(Goal::preconditionsMet)
                .collect(Collectors.toList());
        goal_options = filterWithInformationAboutFriendlyAgents(goal_options);
        Goal goal = selectGoalWithHighestPriority(goal_options);
        if (goal != null) {
            //change goal if priority is higher or the current goal isn't valid anymore
            if (areConditionsForChangingGoalFulfilled(goal)) {
                informOthersOfPossibleTaskChange(goal);
                connectPingCommunicator(goal);
                currentGoal = goal;
            }
        }
        return currentGoal;
    }



    private void revalidateStrategy() {
        if (perceptionAndMemory.getCurrentStep() > 5){
            if (strategyModule.getOffensivePercentage() > configuration.getMaxOffensivePercentage()){
                this.strategy = Strategy.DEFENSE;

            }
        }
        strategyModule.broadcastMyStrategy(strategy);
    }


    private List<Goal> generateGoals() {
        return configuration.generateGoals(perceptionAndMemory, strategy);
    }

    private double priority(Goal goal) {
        double priority = configuration.priority(goal, strategy);
        if (goal instanceof GoalWithPriorityOffset) {
            priority = priority + ((GoalWithPriorityOffset) goal).getPriorityOffset();
        }
        return priority;
    }

    private Goal selectGoalWithHighestPriority(List<Goal> goal_options) {
        Goal goal = new G6GoalExplore(perceptionAndMemory);
        for (var goalCandidate : goal_options) {
            if (priority(goalCandidate) > priority(goal)) {
                goal = goalCandidate;
            }
        }
        return goal;
    }

    private List<Goal> filterWithInformationAboutFriendlyAgents(List<Goal> goal_options) {
        List<Goal> filteredGoals = new ArrayList<>();
        for (Goal goal : goal_options) {
            if (goal instanceof GoalWithTask goalWithTask) {
                List<AuctionModuleEntry> correspondingTasksAccepted =
                        communicationModule.getTaskAuctionModule().getEntries()
                                .stream()
                                .filter((entry) -> (entry.subTaskWithCost().taskname().equals(goalWithTask.getTaskname()) //taskname matches
                                        && entry.subTaskWithCost().blockIndex() == goalWithTask.getAcceptedBlockIndex()   //blockindex matches
                                        && entry.subTaskWithCost().cost() > (defineCost(goalWithTask)))).toList();        //has higher cost than this goal

                if (correspondingTasksAccepted.isEmpty()) {
                    filteredGoals.add(goalWithTask);
                }
            } else {
                filteredGoals.add(goal);
            }
        }
        return filteredGoals;
    }

    private int defineCost(GoalWithTask goalWithTask) {
        Task task = fetchMatchingTask(goalWithTask);

        if (task == null) return Integer.MAX_VALUE; //is no valid task
        if (task.getRequirements().size() < goalWithTask.getAcceptedBlockIndex())
            return Integer.MAX_VALUE; //Block Index not in Task
        Block block = task.getRequirements().get(goalWithTask.getAcceptedBlockIndex());
        for (Block attachedBlock : perceptionAndMemory.getDirectlyAttachedBlocks()) {
            if (attachedBlock.getCoordinates().equals(block.getCoordinates())) return -1; //block already attached
        }
        return distanceToClosestDispenser(block);
    }

    private int distanceToClosestDispenser(Block block) {
        List<Block> matchingDispensers = perceptionAndMemory.getDispensers()
                .stream()
                .filter((dispenser) -> dispenser.getBlocktype().equals(block.getBlocktype()))
                .sorted().toList();
        if (matchingDispensers.isEmpty()) return Integer.MAX_VALUE; //no dispenser known
        return matchingDispensers.get(0).getCoordinates().manhattanDistanceTo(new Point(0, 0)); //the distance to the closest dispenser
    }

    private Task fetchMatchingTask(GoalWithTask goalWithTask) {
        for (Task task : perceptionAndMemory.getActiveTasks()) {
            if (task.getName().equals(goalWithTask.getName())) return task;
        }
        return null;
    }

    private boolean areConditionsForChangingGoalFulfilled(Goal goal) {
        return priority(goal) > priority(currentGoal)
                || currentGoal.isFullfilled()
                || !currentGoal.isSucceding()
                || (currentGoal instanceof GoalWithTask currentGoalWithTask && (
                            isCurrentGoalNoLongerAssociatedToAgent(currentGoalWithTask)
                        || perceptionAndMemory.getAllTasks().stream().noneMatch(
                            task -> task.getName().equals(currentGoalWithTask.getTaskname()))));
    }

    private boolean isCurrentGoalNoLongerAssociatedToAgent(GoalWithTask currentGoal) {
        if (taskAuctionModule.getMySubTask() == null) return true;                          //case no Task accepted yet
        SubTaskWithCost currentSubtask = taskAuctionModule.getMySubTask();
        // is an instance of the accepted goal
        return !(currentSubtask.taskname().equals(currentGoal.getTaskname()) && currentSubtask.blockIndex() == currentGoal.getAcceptedBlockIndex());
    }

    private void informOthersOfPossibleTaskChange(Goal goal) {
        if (goal instanceof GoalWithTask goalWithTask) {
            acceptTask(goalWithTask);
        } else {
            taskAuctionModule.sendTaskNoLongerValid();
        }
    }

    private void acceptTask(GoalWithTask goalWithTask) {
        taskAuctionModule.acceptTask(new SubTaskWithCost(
                goalWithTask.getName(),
                goalWithTask.getAcceptedBlockIndex(),
                defineCost(goalWithTask)
        ));    }

    private void connectPingCommunicator(Goal goal) {
        pingCommunicator.setPingReceiver(
                goal instanceof PingReceiver receiver? receiver : null // if the goal wants to send and receive pings add, set to goal, else remove the old receiver
        );
        if(goal instanceof PingReceiver receiver) receiver.addPingListener(pingCommunicator);
    }
}
