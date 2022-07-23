package g6Agent.perceptionAndMemory;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.agents.Agent;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.communicationModule.CommunicationModuleImplementation;
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
public class AttachedBlocksModule extends Agent implements LastActionListener {

    private HashMap<String, Block> attachedBlocks;
    private final PerceptionAndMemory perceptionAndMemory;
    private  CommunicationModule communicationModule = null;

    public AttachedBlocksModule( String name, MailService mailbox ) {
        super(name, mailbox);
        this.attachedBlocks = new HashMap<>(4);
        PerceptionAndMemoryLinker linker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = linker.getPerceptionAndMemory();
        this.communicationModule = new CommunicationModuleImplementation(name, mailbox);
    }

    public AttachedBlocksModule( PerceptionAndMemory perceptionAndMemory) {
        super();
        this.perceptionAndMemory = perceptionAndMemory;

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
        String otherAgentName = ((Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)).toProlog();
        Integer blockPositionX;
        Integer blockPositionY;
        Point point = null;

        try {
            blockPositionX = Integer.parseInt(((Identifier) ((ParameterList) lastAction.getParameters().get(1)).get(1)).toProlog());
            blockPositionY = Integer.parseInt(((Identifier) ((ParameterList) lastAction.getParameters().get(2)).get(2)).toProlog());
            point = new Point(blockPositionX, blockPositionY);
        } catch (NumberFormatException e) { }

        if (point != null) {
            for (Block block : perceptionAndMemory.getAttachedBlocks()) {
                if (block.getCoordinates().equals(point)) {
                    Block blockToConnect = new Block(point, block.getBlocktype());
                    attachedBlocks.put(point.toString(), blockToConnect);
                    break;
                }
            }
        }

        // Notification of the other agent about that, the blocks being changed
        this.communicationModule.broadcastActionConnect(otherAgentName, point);

    }

    private void disconnectBlocks(LastActionMemory lastAction) {
        String parameter = ((Identifier) ((ParameterList) lastAction.getParameters().get(0)).get(0)).toProlog();
        Point  attachment1 = null;
        Point attachment2 = null;
        boolean isAttachment1 = false;

        try {
            Integer blockPositionXAttachment1 = Integer.parseInt(((Identifier) ((ParameterList) lastAction.getParameters().get(1)).get(1)).toProlog());
            Integer blockPositionYAttachment1 = Integer.parseInt(((Identifier) ((ParameterList) lastAction.getParameters().get(2)).get(2)).toProlog());
            Integer blockPositionXAttachment2 = Integer.parseInt(((Identifier) ((ParameterList) lastAction.getParameters().get(3)).get(3)).toProlog());
            Integer blockPositionYAttachment2 = Integer.parseInt(((Identifier) ((ParameterList) lastAction.getParameters().get(4)).get(4)).toProlog());
            attachment1 = new Point(blockPositionXAttachment1, blockPositionYAttachment1);
            attachment2 = new Point(blockPositionXAttachment2, blockPositionYAttachment2);
        }
        catch(NumberFormatException e) { }

        if (attachment1 != null && attachment2 != null) {
            for (Block block : perceptionAndMemory.getAttachedBlocks()) {
                if (block.getCoordinates().equals(attachment1)) {
                    if(!isAttachment1){
                        Block blockToDisconnect = new Block(attachment1, block.getBlocktype());
                        attachedBlocks.remove(attachment1.toString(), blockToDisconnect);
                        isAttachment1 = true;
                    }
                    else if(isAttachment1){
                        Block blockToDisconnect = new Block(attachment2, block.getBlocktype());
                        attachedBlocks.remove(attachment2.toString(), blockToDisconnect);
                        break;
                    }
                }
            }
        }
        // Notification of the other agent about that, the blocks being changed
        this.communicationModule.broadcastActionDisconnect(this.getName(),  attachment1, attachment2);
    }


    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        return null;
    }

    @Override
    public void handleMessage(Percept message, String sender) {

    }
}
