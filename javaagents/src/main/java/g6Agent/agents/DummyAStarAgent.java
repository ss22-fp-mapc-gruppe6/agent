package g6Agent.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.actions.G6Action;
import g6Agent.decisionModule.AStar;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;
import g6Agent.services.Point;

import java.util.List;


public class DummyAStarAgent extends Agent {
    private final PerceptionAndMemory perceptionAndMemory;

    public DummyAStarAgent(String name, MailService mailbox) {
        super(name, mailbox);
        PerceptionAndMemoryLinker linker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = linker.getPerceptionAndMemory();
    }

    @Override
    public void handlePercept(Percept percept) {

    }

    @Override
    public Action step() {
        perceptionAndMemory.handlePercepts(getPercepts());

        final List<Point> obstacles = perceptionAndMemory.getObstacles();
        final List<Block> directlyAttachedBlocks = perceptionAndMemory.getDirectlyAttachedBlocks();
        final List<Integer> movementSpeed = perceptionAndMemory.getCurrentRole().getMovementSpeed();
        final Integer stepSize = movementSpeed.get(directlyAttachedBlocks.size());

        final Point target = new Point(99, 99);
        final List<? extends G6Action> shortestPathActions = AStar.findShortestPath(target, obstacles, stepSize);
        return (Action) shortestPathActions.get(0);
    }

    @Override
    public void handleMessage(Percept message, String sender) {
    }
}
