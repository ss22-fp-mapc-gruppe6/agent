package g6Agent.communicationModule.entities;

/**
 *
 * @param taskname the Name of the Task
 * @param blockIndex the Index of the Block in the requirements of the Task
 * @param cost the cost associated with this subtask
 */
public record SubTaskWithCost(String taskname, int blockIndex, int cost) {
}
