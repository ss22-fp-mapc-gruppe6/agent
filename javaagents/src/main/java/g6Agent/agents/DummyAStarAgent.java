package g6Agent.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;
import g6Agent.services.Point;


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

        final Point target = new Point(99, 99);
        return (Action) AStar.astarNextStep(target, perceptionAndMemory);
    }

    @Override
    public void handleMessage(Percept message, String sender) {
    }
}
