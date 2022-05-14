package g6Agent.actions.Objects;


import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Enties.Block;

import java.util.HashSet;
import java.util.UUID;

public class Pair_BlockAgent {
    private Block block;
    private MyTestAgent agent;
    private UUID id ;

    public Pair_BlockAgent(Block block, MyTestAgent agent)
    {
        this.agent = agent;
        this.block = block;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {return id;}

    public MyTestAgent getAgent() {return this.agent;}

    public Block getBlock() {return block;}
}
