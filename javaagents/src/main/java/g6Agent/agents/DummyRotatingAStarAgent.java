package g6Agent.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.actions.*;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryLinker;
import g6Agent.services.Direction;
import g6Agent.services.Point;

import java.util.List;

import static g6Agent.services.Point.byDistanceToOrigin;


public class DummyRotatingAStarAgent extends Agent {
    private final PerceptionAndMemory perceptionAndMemory;
    private final String b0String = "b0";
    private final String d0String = "d0";

    public DummyRotatingAStarAgent(String name, MailService mailbox) {
        super(name, mailbox);
        PerceptionAndMemoryLinker linker = new PerceptionAndMemoryLinker(this, mailbox);
        this.perceptionAndMemory = linker.getPerceptionAndMemory();
    }

    @Override
    public void handlePercept(Percept percept) {

    }

    Action becomeworker() {
        final List<Point> roleZones = perceptionAndMemory.getRoleZones();
        if (roleZones.contains(new Point(0, 0))) {
            System.out.println("became worker");
            return new Adopt("worker");
        }
        final Point point = roleZones.stream()
                .min(byDistanceToOrigin())
                .orElseThrow(() -> new IllegalStateException("no role zone in sight?"));
        final List<G6Action> g6Actions = AStar.astarShortestPathWithAgents(point, perceptionAndMemory);
        if (!g6Actions.isEmpty()) return (Action) g6Actions.get(0);
        return null;
    }

    Action goToAndPickUpBlock() {
        var b0 = perceptionAndMemory.getDispensers()
                .stream()
                .filter(block -> block.getBlocktype().equals(b0String))
                .map(Block::getCoordinates)
                .min(byDistanceToOrigin())
                .orElseThrow(() -> new IllegalStateException("should have seen the block"));

        var actions = AStar.astarShortestPathWithAgents(b0.add(0, 1), perceptionAndMemory);
        if (!actions.isEmpty()) return ((Action) actions.get(0));
        else {
            Direction direction = Direction.fromAdjacentPoint(b0);
            final var lastAction = perceptionAndMemory.getLastAction();
            if (lastAction.getName().equalsIgnoreCase("request") &&
                    lastAction.getSuccessMessage().equalsIgnoreCase("success")
            ) return new Attach(direction);
            return new Request(direction);
        }
    }

    @Override
    public Action step() {
        perceptionAndMemory.handlePercepts(getPercepts());
        final String currentRoleName = perceptionAndMemory.getCurrentRole().getName();
        if (currentRoleName.equalsIgnoreCase("default")) {
            return becomeworker();
        } else if (currentRoleName.equalsIgnoreCase("worker")
                && perceptionAndMemory.getDirectlyAttachedBlocks().isEmpty()) {
            return goToAndPickUpBlock();
        } else {
            var d0 = perceptionAndMemory.getDispensers().stream()
                    .filter(block -> block.getBlocktype().equals(d0String))
                    .map(Block::getCoordinates)
                    .min(byDistanceToOrigin());
            var target = d0.orElseThrow(() -> new IllegalStateException("should have seen the block"));
            final var g6Action = AStar.astarNextStepWithAgents(target, perceptionAndMemory).orElse(new Skip());
            return (Action) g6Action;
        }
    }


    @Override
    public void handleMessage(Percept message, String sender) {
    }

    @Override
    public void handlePerceptionforStep() {

    }

    @Override
    public void handleSyncRequests() {

    }

    @Override
    public void initialiseSync() {

    }
}
