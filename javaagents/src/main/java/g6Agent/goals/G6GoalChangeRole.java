package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.decisionModule.astar.AStar;
import g6Agent.decisionModule.manhattanDistanceMove.ManhattanDistanceMove;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;

public class G6GoalChangeRole implements Goal {
    String roleName;
    PerceptionAndMemory perceptionAndMemory;
    Point roleZoneMovedToLast;

    public G6GoalChangeRole(String roleName, PerceptionAndMemory perceptionAndMemory) {
        this.roleName = roleName;
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        if (!perceptionAndMemory.getRoleZones().isEmpty()) {
            boolean isInRoleZone = checkIfInRoleZone();
            if (isInRoleZone) return new Adopt(roleName);

            return moveToRoleZone();
        }
        return null;
    }

    private G6Action moveToRoleZone() {
        Point closestRoleZone = perceptionAndMemory.getRoleZones().get(0);
        for (Point rolelZone : perceptionAndMemory.getRoleZones()) {
            if (rolelZone.manhattanDistanceTo(new Point(0, 0)) < closestRoleZone.manhattanDistanceTo(new Point(0, 0))) {
                if (roleZoneMovedToLast == null) {
                    closestRoleZone = rolelZone;
                } else {
                    if (rolelZone.manhattanDistanceTo(closestRoleZone) < closestRoleZone.manhattanDistanceTo(roleZoneMovedToLast)) {
                        closestRoleZone = rolelZone;
                    }
                }
            }
        }
        roleZoneMovedToLast = closestRoleZone;

        G6Action moveToRoleZone = AStar
                .astarNextStep(closestRoleZone, perceptionAndMemory)
                .orElse(ManhattanDistanceMove.nextAction(closestRoleZone, perceptionAndMemory));
        if(moveToRoleZone.predictSuccess(perceptionAndMemory)) return moveToRoleZone;
        return AStar.astarNextStepWithAgents(closestRoleZone, perceptionAndMemory)
                .orElse(ManhattanDistanceMove.nextAction(closestRoleZone, perceptionAndMemory));
    }

    private boolean checkIfInRoleZone() {
        boolean inGoalZone = false;
        for (Point roleZone : perceptionAndMemory.getRoleZones()) {
            if (roleZone.equals(new Point(0, 0))) {
                inGoalZone = true;
            }
        }
        return inGoalZone;
    }

    @Override
    public boolean isSucceding() {
        return !perceptionAndMemory.getRoleZones().isEmpty();
    }

    @Override
    public boolean isFullfilled() {
        return perceptionAndMemory.getCurrentRole().getName().equals(roleName);
    }

    @Override
    public String getName() {
        return "G6GoalChangeRole";
    }

    @Override
    public boolean preconditionsMet() {
        if (perceptionAndMemory.getRoleZones().isEmpty()) return false;
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        return !perceptionAndMemory.getCurrentRole().getName().equals(this.roleName);
    }
}
