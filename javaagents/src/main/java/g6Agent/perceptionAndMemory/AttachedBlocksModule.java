package g6Agent.perceptionAndMemory;

import eis.iilang.Identifier;
import eis.iilang.ParameterList;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Rotation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Module to keep track of the Blocks attached to an Agent.
 */
public class AttachedBlocksModule implements LastActionListener {

    private HashMap<String, Block> attachedBlocks;
    private final PerceptionAndMemory perceptionAndMemory;

    public AttachedBlocksModule(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
        this.attachedBlocks = new HashMap<>(4);
    }

    @Override
    public void reportLastAction(LastActionMemory lastAction) {
        if (lastAction != null && lastAction.getSuccessMessage().equals("success")) {
            switch (lastAction.getName()) {
                case "attach" -> attachBlock(lastAction);
                case "rotate" -> rotateBlocks(lastAction);
                case "detach" -> detachBlock(lastAction);
                case "submit" -> submitBlocks(lastAction);
                case "connect" -> {
                }    //TODO if Action is ever used, communication with target agent?
                case "disconnect" -> {
                } //TODO if Action is ever used
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
}
