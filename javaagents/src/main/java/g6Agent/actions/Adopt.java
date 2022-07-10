package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Adopt extends Action implements G6Action {
    /**
     * If in a rolezone this action let's the worker adopt the specified Role
     *
     * @param role The name of the role to adopt.
     */
    private String role;
    public Adopt(String role) {
        super("adopt", new Identifier(role));
        this.role = role;
    }
    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) throws Exception {
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        boolean isRole = perceptionAndMemory.getRoleZones().stream().anyMatch(x -> x.equals(role));
        return (isRole);
    }
}
