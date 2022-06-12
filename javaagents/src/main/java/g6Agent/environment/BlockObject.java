package g6Agent.environment;

import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.services.ObjectType;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class BlockObject extends CellObject {

    private Block block;
    private boolean attached = false;
    private final HashSet<MyTestAgent> attachedTo = new HashSet<>();

    BlockObject(ObjectType objectType) {
        super(objectType.block,new ConcurrentHashMap<>() );
    }
}
