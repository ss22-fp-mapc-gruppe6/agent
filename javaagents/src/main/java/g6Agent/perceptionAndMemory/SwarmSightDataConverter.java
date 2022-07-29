package g6Agent.perceptionAndMemory;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Percept;
import g6Agent.perceptionAndMemory.Enties.AgentNameAndPosition;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Vision;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemoryInput;
import g6Agent.services.Point;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * Class to convert the Data from {@link SwarmSightController} to {@link g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory}
 * and synchronise the Data
 *
 * Is Part of {@link SwarmSightController}
 *
 * @author Kai Müller
 */

public class SwarmSightDataConverter {
    private final PerceptionAndMemory perceptionAndMemory;
    private final PerceptionAndMemoryInput perceptionAndMemoryInput;


    public SwarmSightDataConverter(PerceptionAndMemoryInput perceptionAndMemoryInput, PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemoryInput = perceptionAndMemoryInput;
        this.perceptionAndMemory = perceptionAndMemory;
    }

    public void updateMyVisionWithSightingsOfOtherAgents(SwarmSightModel swarmSightModel, HashMap<String, Vision> visions) {
        HashSet<Block> uniqueDispensers = new HashSet<>();
        HashSet<Block> uniqueBlocks = new HashSet<>();
        HashSet<Point> uniqueGoalZones = new HashSet<>();
        HashSet<Point> uniqueRoleZones = new HashSet<>();
        HashSet<Point> uniqueObstacles = new HashSet<>();
        for (AgentNameAndPosition agent : swarmSightModel.knownAgents()) {
            if (agent.position() != null) {
               Vision vison = visions.get(agent.name());
                if (vison != null) {
                    addVisionToHashSets(uniqueDispensers, uniqueBlocks, uniqueGoalZones, uniqueRoleZones, uniqueObstacles, agent, vison);
                }
            }
        }
        flushHashSetsToPerceptionAndMemory(uniqueDispensers, uniqueBlocks, uniqueGoalZones, uniqueRoleZones, uniqueObstacles);
    }


    private void addVisionToHashSets(HashSet<Block> uniqueDispensers, HashSet<Block> uniqueBlocks, HashSet<Point> uniqueGoalZones, HashSet<Point> uniqueRoleZones, HashSet<Point> uniqueObstacles, AgentNameAndPosition agent, Vision vison) {
        //add dispensers to uniqueDispensers
        for (Block dispenser : vison.dispensers()) {
            Block dispenserWithNewPosition =
                    new Block(dispenser.getCoordinates().add(agent.position()), dispenser.getBlocktype());
            if (isOutOfSight(dispenserWithNewPosition)) {
                uniqueDispensers.add(dispenserWithNewPosition);
            }
        }
        //add blocks to uniqueBlocks
        for (Block block : vison.blocks()) {
            Block blockWithNewPosition =
                    new Block(block.getCoordinates().add(agent.position()), block.getBlocktype());
            if (isOutOfSight(blockWithNewPosition)) {
                uniqueBlocks.add(blockWithNewPosition);
            }
        }
        //add goalzones to uniqueGoalzones
        for (Point goalZone : vison.goalZones()) {
            Point goalZoneWithNewPosition = goalZone.add(agent.position());
            if (isOutOfSight(goalZoneWithNewPosition)) {
                uniqueGoalZones.add(goalZoneWithNewPosition);
            }
        }
        //add rolezones to uniqueRoleZones
        for (Point roleZone : vison.roleZones()) {
            Point roleZoneWithNewPosition = roleZone.add(agent.position());
            if (isOutOfSight(roleZoneWithNewPosition)) {
                uniqueRoleZones.add(roleZoneWithNewPosition);
            }
        }
        //add obstacles to uniqueObstacles
        for (Point obstacle : vison.obstacles()) {
            Point obstacleWithNewPosition = obstacle.add(agent.position());
            if (isOutOfSight(obstacleWithNewPosition)) {
                uniqueObstacles.add(obstacleWithNewPosition);
            }
        }
    }

    private void flushHashSetsToPerceptionAndMemory(HashSet<Block> uniqueDispensers, HashSet<Block> uniqueBlocks, HashSet<Point> uniqueGoalZones, HashSet<Point> uniqueRoleZones, HashSet<Point> uniqueObstacles) {
        for (Block dispenser : uniqueDispensers) {
            try {
                addDispenserToPerception(dispenser);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding dispenser");
                e.printStackTrace();
            }
        }
        for (Block block : uniqueBlocks) {
            try {
                addBlockToPerception(block);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding block");
                e.printStackTrace();
            }
        }
        for (Point obstacle : uniqueObstacles) {
            try {
                addObstacleToPerception(obstacle);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding obstacle");
                e.printStackTrace();
            }
        }
        for (Point goalzone : uniqueGoalZones) {
            try {
                addGoalZoneToPerception(goalzone);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding goalZone");
                e.printStackTrace();
            }
        }
        for (Point roleZone : uniqueRoleZones) {
            try {
                addRoleZoneToPerception(roleZone);
            } catch (Exception e) {
                System.out.println(" IN UPDATE VISION : Problem with adding roleZone");
                e.printStackTrace();
            }
        }
    }


    private void addRoleZoneToPerception(Point roleZone) throws Exception {
        perceptionAndMemoryInput.handleRoleZone(new Percept(
                "roleZone",
                new Numeral(roleZone.x),
                new Numeral(roleZone.y)
        ));
    }

    private void addGoalZoneToPerception(Point goalzone) throws Exception {
        perceptionAndMemoryInput.handleGoalZone(new Percept(
                "goalZone",
                new Numeral(goalzone.x),
                new Numeral(goalzone.y)
        ));
    }

    private void addObstacleToPerception(Point obstacle) throws Exception {
        perceptionAndMemoryInput.handleThingPercept(
                new Percept("thing",
                        new Numeral(obstacle.x),
                        new Numeral(obstacle.y),
                        new Identifier("obstacle"),
                        new Identifier("")
                )
        );
    }

    private void addBlockToPerception(Block block) throws Exception {
        perceptionAndMemoryInput.handleThingPercept(
                new Percept("thing",
                        new Numeral(block.getCoordinates().x),
                        new Numeral(block.getCoordinates().y),
                        new Identifier("block"),
                        new Identifier(block.getBlocktype())
                )
        );
    }


    private void addDispenserToPerception(Block dispenser) throws Exception {
        perceptionAndMemoryInput.handleThingPercept(
                new Percept("thing",
                        new Numeral(dispenser.getCoordinates().x),
                        new Numeral(dispenser.getCoordinates().y),
                        new Identifier("dispenser"),
                        new Identifier(dispenser.getBlocktype())
                )
        );
    }
    private boolean isOutOfSight(Point point) {
        if (perceptionAndMemory.getCurrentRole() == null) return false; // if no Role ignore
        return point.manhattanDistanceTo(new Point(0, 0)) > perceptionAndMemory.getCurrentRole().getVisionRange();
    }

    private boolean isOutOfSight(Block block) {
        if (perceptionAndMemory.getCurrentRole() == null) return false; // if no Role ignore
        return block.getCoordinates().manhattanDistanceTo(new Point(0, 0)) > perceptionAndMemory.getCurrentRole().getVisionRange();
    }

}
