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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DummyRotatingAStarAgent extends Agent {
    private final PerceptionAndMemory perceptionAndMemory;

    public DummyRotatingAStarAgent(String name, MailService mailbox) {
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
        final String currentRoleName = perceptionAndMemory.getCurrentRole().getName();
        if (currentRoleName.equalsIgnoreCase("default")) {
            final List<Point> roleZones = perceptionAndMemory.getRoleZones();
            if (roleZones.contains(new Point(0, 0))) {
                return new Adopt("worker");
            }
            final Point point = roleZones.stream()
                    .min(byDistanceToOrigin())
                    .orElseThrow(() -> new IllegalStateException("no role zone in sight?"));
            final List<G6Action> g6Actions = AStar.astarShortestPath(point, perceptionAndMemory);
            if (!g6Actions.isEmpty()) return (Action) g6Actions.get(0);
        } else if (currentRoleName.equalsIgnoreCase("worker") && perceptionAndMemory.getDirectlyAttachedBlocks().isEmpty()) {
            var b0 = perceptionAndMemory.getDispensers()
                    .stream()
                    .filter(block -> block.getBlocktype().equals("b0"))
                    .map(Block::getCoordinates)
                    .min(byDistanceToOrigin())
                    .orElseThrow(() -> new IllegalStateException("should have seen the block"));


            var actions = AStar.astarShortestPath(b0.add(0, 1), perceptionAndMemory);
            if (!actions.isEmpty())
                return ((Action) actions.get(0));
            else {
                Direction direction = Direction.fromAdjacentPoint(b0);
                final var lastAction = perceptionAndMemory.getLastAction();
                if (lastAction.getName().equalsIgnoreCase("request") &&
                        lastAction.getSuccessMessage().equalsIgnoreCase("success")
                ) {
                    return new Attach(direction);
                }

                return new Request(direction);
            }
        } else {
            var b1 = perceptionAndMemory.getDispensers().stream()
                    .filter(block -> block.getBlocktype().equals("b1"))
                    .map(Block::getCoordinates)
                    .min(byDistanceToOrigin());
            var target = b1.orElseThrow(() -> new IllegalStateException("should have seen the block"));
            final var g6Action = AStar.astarNextStep(target, perceptionAndMemory);
            return (Action) g6Action;
        }
        return new Skip();
    }

    private static Comparator<Point> byDistanceToOrigin() {
        return Comparator.comparingInt(point -> point.manhattanDistanceTo(new Point(0, 0)));
    }

    @Override
    public void handleMessage(Percept message, String sender) {
    }
}
