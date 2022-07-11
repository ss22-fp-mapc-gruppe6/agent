package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Adopt extends Action implements G6Action {

    private final String role;
    /**
     * If in a rolezone this action let's the worker adopt the specified Role
     *
     * @param role The name of the role to adopt.
     */
    public Adopt(String role) {
        super("adopt", new Identifier(role));
        this.role = role;
    }
    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) {

        boolean isRole = perceptionAndMemory.getPossibleRoles().stream().anyMatch(x -> x.getName().equals(role));
        boolean isInRoleZone = perceptionAndMemory.getRoleZones().stream().anyMatch(x -> x.equals(new Point(0,0)));
        return (isRole && isInRoleZone);
    }
}
