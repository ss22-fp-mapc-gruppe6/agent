package g6Agent.communicationModule.submodules;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.communicationModule.entities.AuctionModuleEntry;
import g6Agent.communicationModule.entities.SubTaskWithCost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  handles the auctioning of tasks and subtasks
 */

public class TaskAuctionModule {
    private final String agentName;
    private final HashMap<String, SubTaskWithCost> acceptedTasks;
    private final MailService mailservice;

    public TaskAuctionModule(String agentName, MailService mailService) {
        this.agentName = agentName;
        this.mailservice = mailService;
        this.acceptedTasks = new HashMap<>();
    }


    public void acceptTask(SubTaskWithCost subTaskWithCost){
        if(checkAuction(agentName, subTaskWithCost)){
            sendTaskAndBlockIndex(subTaskWithCost);
        }
    }
    void sendTaskAndBlockIndex(SubTaskWithCost subTaskWithCost) {
        this.acceptedTasks.put(agentName, subTaskWithCost);

        mailservice.broadcast(
                new Percept("MY_TASK",
                        new Identifier(subTaskWithCost.taskname()), // the name of the task
                        new Numeral(subTaskWithCost.blockIndex()), // the Index of the block in the requirements of the task
                        new Numeral(subTaskWithCost.cost())        // the cost associated with this task
                ),
                agentName);
    }

    /**
     * Informs that the Task is no longer valid for this agent
     */
    public void sendTaskNoLongerValid(){
        mailservice.broadcast(new Percept("MY_TASK",
                new Identifier(this.agentName)), this.agentName);
    }

    /**
     * Informs other Agents that the Task of an Agent is marked No longer valid
     * @param otherAgentsName the Name of the Agent
     */
    private void sendTaskNoLongerValid(String otherAgentsName){
        mailservice.broadcast(new Percept("MY_TASK",
                new Identifier(otherAgentsName)), this.agentName);
    }

    public void receiveTaskAndBlockIndex(Percept message, String sender){
        //Message with a new Task
        if (message.getName().equals("MY_TASK") && message.getParameters().size() == 3){
            SubTaskWithCost subTaskWithCost = new SubTaskWithCost(
                    ((Identifier) message.getParameters().get(0)).toProlog(),
                    ((Numeral)message.getParameters().get(1)).getValue().intValue(),
                    ((Numeral) message.getParameters().get(2)).getValue().intValue()
            );
            checkAuction(sender, subTaskWithCost);
        //Message which invalidates Task
        } else if (message.getName().equals("MY_TASK") && message.getParameters().size() == 1) {
            acceptedTasks.remove(
                    ((Identifier) message.getParameters().get(0)).toProlog()
            );
        }
    }

    /**
     *
     * @param sender the agents placing the bet
     * @param subTaskWithCost the accepted Task with its cost
     * @return is the auction won?
     */
    private boolean checkAuction(String sender, SubTaskWithCost subTaskWithCost) {
        List<String> agentsWithHigherCosts = new ArrayList<>();
        List<String> agentsWithLowerCosts = new ArrayList<>();


        fillListsWithAgentsFullfillingTheSameSubTask(subTaskWithCost, agentsWithHigherCosts, agentsWithLowerCosts);
        for (var agent : agentsWithHigherCosts){
            acceptedTasks.remove(agent);
            sendTaskNoLongerValid(agent);
        }
        if (agentsWithLowerCosts.isEmpty()){
            acceptedTasks.put(sender, subTaskWithCost);
            return true;
        }
        return false;
    }

    private void fillListsWithAgentsFullfillingTheSameSubTask(SubTaskWithCost subTaskWithCost, List<String> agentsWithHigherCosts, List<String> agentsWithLowerCosts) {
        acceptedTasks.forEach((key, value) -> {
            //is not self
            if (!key.equals(agentName)) {
                //is working at same task and block indes
                if (subTaskWithCost.taskname().equals(value.taskname()) && subTaskWithCost.blockIndex() == value.blockIndex()) {

                    if (value.cost() <= subTaskWithCost.cost()) {
                        agentsWithLowerCosts.add(key);
                    } else {
                        agentsWithHigherCosts.add(key);
                    }
                }
            }
        });
    }

    public List<String> getAgentsAtSameTask(){
        List<String> agents = new ArrayList<>();
        String myTask = acceptedTasks.get(agentName).taskname();
        if (myTask != null) {
            acceptedTasks.forEach((key, value) -> {
                if (!key.equals(agentName) && value.taskname().equals(myTask)){
                    agents.add(key);
                }
            });
        }
        return agents;
    }

    public SubTaskWithCost getMySubTask() {
        return acceptedTasks.get(agentName);
    }

    public List<AuctionModuleEntry> getEntries(){
        List<AuctionModuleEntry> auctionModuleEntries = new ArrayList<>();
        acceptedTasks.forEach((key,value) -> auctionModuleEntries.add(new AuctionModuleEntry(key, value)));
        return auctionModuleEntries;
    }
}
