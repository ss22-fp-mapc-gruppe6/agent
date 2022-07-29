package g6Agent.perceptionAndMemory.Enties;

import g6Agent.services.Point;

import java.util.List;

/**
 * Record to exchange and store the Parameters important in what an Agent sees
 * @param dispensers    the Dispenser
 * @param blocks        the Blocks
 * @param roleZones     the RoleZones
 * @param goalZones     the GoalZones
 * @param obstacles     theObstacles
 *
 * @author Kai Müller
 */
public record Vision(List<Block> dispensers, List<Block> blocks, List<Point> roleZones, List<Point> goalZones,
              List<Point> obstacles) {
}
