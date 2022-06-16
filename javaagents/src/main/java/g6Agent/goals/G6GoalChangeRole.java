package g6Agent.goals;

import g6Agent.actions.*;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;

public class G6GoalChangeRole implements Goal{
    String roleName;
    PerceptionAndMemory perceptionAndMemory;
    Point roleZoneMovedToLast;

    public G6GoalChangeRole(String roleName, PerceptionAndMemory perceptionAndMemory) {
        this.roleName = roleName;
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        if(!perceptionAndMemory.getRoleZones().isEmpty()){
            boolean isInRoleZone = checkIfInRoleZone();
            if (isInRoleZone) return new Adopt(roleName);

            return moveToRoleZone();
        }
        return null;
    }

    private G6Action moveToRoleZone() {
        Point closestRoleZone = perceptionAndMemory.getRoleZones().get(0);
        for (Point rolelZone : perceptionAndMemory.getRoleZones()){
            if(rolelZone.manhattanDistanceTo(new Point(0,0)) < closestRoleZone.manhattanDistanceTo(new Point(0,0))){
                if (roleZoneMovedToLast == null) {
                    closestRoleZone = rolelZone;
                } else {
                    if (rolelZone.manhattanDistanceTo(closestRoleZone) < closestRoleZone.manhattanDistanceTo(roleZoneMovedToLast)){
                        closestRoleZone = rolelZone;
                    }
                }
            }
        }
        roleZoneMovedToLast = closestRoleZone;

        Direction direction = Direction.WEST;
        for (Direction d : Direction.allDirections()){
            if (d.getNextCoordinate().manhattanDistanceTo(closestRoleZone) < direction.getNextCoordinate().manhattanDistanceTo(closestRoleZone)){

                direction = d;
            }
        }
        return moveTo(direction);
    }

    private G6Action moveTo(Direction direction) {
        for(Block attachedBlock : perceptionAndMemory.getDirectlyAttachedBlocks()){
            if(!attachedBlock.getCoordinates().invert().equals(direction.getNextCoordinate())){
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        for(Point obstacle : perceptionAndMemory.getObstacles()){
            if(direction.getNextCoordinate().equals(obstacle)){
                return new Clear(obstacle);
            }
        }
        return new Move(direction);
    }
    private boolean checkIfInRoleZone() {
        boolean inGoalZone = false;
        for(Point roleZone : perceptionAndMemory.getRoleZones()) {
            if(roleZone.equals(new Point(0,0))){
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
