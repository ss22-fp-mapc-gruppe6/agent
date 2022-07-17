package g6Agent.perceptionAndMemory;

import eis.iilang.Identifier;
import eis.iilang.ParameterList;
import g6Agent.agents.Agent;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.perceptionAndMemory.Enties.AgentNameAndPosition;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Module to keep track of the Blocks attached to an Agent.
 */
public class AttachedBlocksModule implements LastActionListener {

    private HashMap<String, Block> attachedBlocks;
    private final PerceptionAndMemory perceptionAndMemory;
    private final CommunicationModule communicationModule;

    public AttachedBlocksModule(PerceptionAndMemory perceptionAndMemory, CommunicationModule communicationModule) {
        this.perceptionAndMemory = perceptionAndMemory;
        this.attachedBlocks = new HashMap<>(4);
        this.communicationModule = communicationModule;
    }

    @Override
    public void reportLastAction(LastActionMemory lastAction) {
        if (lastAction != null && lastAction.getSuccessMessage().equals("success")) {
            switch (lastAction.getName()) {
                case "attach" -> attachBlock(lastAction);
                case "rotate" -> rotateBlocks(lastAction);
                case "detach" -> detachBlock(lastAction);
                case "submit" -> submitBlocks(lastAction);
                case "connect" -> connectBlock(lastAction);
                case "disconnect" -> disconnectBlocks(lastAction);
                default ->System.out.println("Error. Name of lastAction does not exist.");
            }
        }
    }

    private void submitBlocks(LastActionMemory lastAction) {
        String parameter = ((Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)).toProlog();
        Task task = null;
        for (Task t : perceptionAndMemory.getAllTasks()) {
            if (t.getName().equals(parameter)) {
                task = t;
                break;
            }
        }
        if (task != null) {
            for (Block block : task.getRequirements()) {
                if (attachedBlocks.get(block.getCoordinates().toString()) == null) {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        System.out.println("ATTACHED BLOCKS CONTROLLER - submit without tracked attached block");
                        e.printStackTrace();
                    }
                }
                attachedBlocks.remove(block.getCoordinates().toString());
            }
        }
    }

    private void detachBlock(LastActionMemory lastAction) {
        Direction direction = Direction.fromIdentifier(
                (Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)
        );
        attachedBlocks.remove(direction.getNextCoordinate().toString());
    }

    private void rotateBlocks(LastActionMemory lastAction) {
        HashMap<String, Block> updatedBlockPositions = new HashMap<>(4);
        String parameter = ((Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)).toProlog();
        Rotation rotation = parameter.equals("cw") ? Rotation.CLOCKWISE : Rotation.COUNTERCLOCKWISE;
        attachedBlocks.forEach((key, block) -> {
                    Block rotatedBlock = new Block(
                            block.getCoordinates().rotate(rotation),
                            block.getBlocktype()
                    );
                    updatedBlockPositions.put(rotatedBlock.getCoordinates().toString(), rotatedBlock);
                }
        );
        this.attachedBlocks = updatedBlockPositions;
    }

    private void attachBlock(LastActionMemory lastAction) {
        Direction direction = Direction.fromIdentifier(
                (Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)
        );
        for (Block block : perceptionAndMemory.getAttachedBlocks()) {
            if (block.getCoordinates().equals(direction.getNextCoordinate())) {
                Block blockToAdd = new Block(direction.getNextCoordinate(), block.getBlocktype());
                attachedBlocks.put(blockToAdd.getCoordinates().toString(), blockToAdd);
                break;
            }
        }
    }

    /**
     * @return the Blocks attached to this agent
     */
    public List<Block> getAttachedBlocks() {
        List<Block> attachedBlocksList = new ArrayList<>(4);
        attachedBlocks.forEach((key, value) -> attachedBlocksList.add(value));
        return attachedBlocksList;
    }

    /**
     * checks if the agent was deactivated and if yes - clears the attached blocks
     */
    public void checkClearConditions() {
        if (perceptionAndMemory.isDeactivated() || perceptionAndMemory.getEnergy() == 0) {
            this.attachedBlocks.clear();
        }
    }
    private void connectBlock(LastActionMemory lastAction) {

        String parameter = ((Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)).toProlog();
        String otherAgentName = null;
        Point position = null;
        Block block = null;
        Block blockToConnect = null;
        HashMap<String, Block> updatedBlockPositions = new HashMap<>(4);

        // Agents name  is specified by action parameters
        for (AgentNameAndPosition agent: perceptionAndMemory.getKnownAgents()) {
            if (agent.getName().contains(parameter)) {
                otherAgentName = agent.getName();
                break;
            }
        }

        for (Block block1: perceptionAndMemory.getDirectlyAttachedBlocks()) {
                if (block1.toString().contains(parameter)) {
                position = block1.getCoordinates();
                break;
            }
        }

        // Each agent is responsible for keeping track of which blocks they are carrying.
        if(otherAgentName != null) {
            block= this.attachedBlocks.get(otherAgentName);
        }

        if(block != null && otherAgentName != null && position != null) {
            // The other agent must adjust its connected block.
            blockToConnect = new Block(
                    position,
                    block.getBlocktype()
            );
            attachedBlocks.replace(otherAgentName, blockToConnect);
        }

        // Notification of the other agent about that, the blocks being changed
        this.communicationModule.broadcastActionConnect(otherAgentName, position, blockToConnect);

    }

    private void disconnectBlocks(LastActionMemory lastAction) {
        String parameter = ((Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)).toProlog();
        Block attachment1 = null;
        Block attachment2 = null;
        String agent1Name = null;
        String agent2Name = null;

        // Two attachments (blocks) of the agent are specified by action parameters
        for (Block block : perceptionAndMemory.getAttachedBlocks()) {
            if (block.getCoordinates().toString().contains(parameter) && attachment1 == null) {
                // Get key (agents name) from value in HashMap
                agent1Name = String.valueOf(attachedBlocks.entrySet().stream().filter(entry -> entry.getValue().equals(block)).map(Map.Entry::getKey)
                        .findFirst());
                if(agent1Name != null) {
                    attachment1 = block;
                    // The agent must adjust its disconnected block.
                    attachedBlocks.remove(agent1Name, attachment1);
                }
            }
            // This same activity with second attachment.
            else if (block.getCoordinates().toString().contains(parameter)) {
                agent2Name = String.valueOf(attachedBlocks.entrySet().stream().filter(entry -> entry.getValue().equals(block)).map(Map.Entry::getKey)
                        .findFirst());
                if(agent2Name != null) {
                    attachment2 = block;
                    attachedBlocks.remove(agent2Name, attachment2);
                }
                break;
            }
        }
        // Notification of the other agent about that, the blocks being changed
        this.communicationModule.broadcastActionDisconnect(agent1Name, agent2Name, attachment1, attachment2);

    }



}
