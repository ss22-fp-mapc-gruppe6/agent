package g6Agent.perceptionAndMemory;

import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;

import java.util.List;

public class AttachedBlocksController implements LastActionListener {
    private List<Block> attachedBlocks;


    @Override
    public void reportLastAction(LastActionMemory lastAction) {

    }
}
