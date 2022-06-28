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
import java.util.List;
import java.util.Optional;


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
            Collections.sort(roleZones, Point::manhattanDistanceTo);
            final Point point = roleZones.stream().findFirst().orElseThrow(() -> new IllegalStateException("no role zone in sight?"));
            final List<G6Action> g6Actions = AStar.astarShortestPath(point, perceptionAndMemory);
            if (!g6Actions.isEmpty()) return (Action) g6Actions.get(0);
        } else if (currentRoleName.equalsIgnoreCase("worker") && perceptionAndMemory.getDirectlyAttachedBlocks().isEmpty()) {
            final Optional<Block> b0 = perceptionAndMemory.getDispensers().stream().filter(block -> block.getBlocktype().equals("b0")).findFirst();
            final Point target = b0.orElseThrow(() -> new IllegalStateException("should have seen the block")).getCoordinates();

            var actions = AStar.astarShortestPath(target.add(0,1), perceptionAndMemory);
            if(!actions.isEmpty())
                return ((Action) actions.get(0));
            else {
                Point coordinates = b0.get().getCoordinates();
                Direction direction = Direction.fromAdjacentPoint(coordinates);
                final var lastAction = perceptionAndMemory.getLastAction();
                if (lastAction.getName().equalsIgnoreCase("request") &&
                        lastAction.getSuccessMessage().equalsIgnoreCase("success")
                ){
                    return new Attach(direction);
                }

                return new Request(direction);
            }
        } else {
            final Optional<Block> b1 = perceptionAndMemory.getDispensers().stream().filter(block -> block.getBlocktype().equals("b1")).findFirst();
            var target = b1.orElseThrow(() -> new IllegalStateException("should have seen the block")).getCoordinates();
            final var g6Action = AStar.astarNextStep(target, perceptionAndMemory);
            return (Action) g6Action;
        }
        return new Skip();
    }

    @Override
    public void handleMessage(Percept message, String sender) {
    }
}
