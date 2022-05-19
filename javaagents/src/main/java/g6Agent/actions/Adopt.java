package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import g6Agent.perceptionAndMemory.Enties.Role;

public class Adopt extends Action implements G6Action{
    /**
     * If in a rolezone this action let's the worker adopt the specified Role
     * @param role The name of the role to adopt.
     */
    public Adopt(String role) {
        super("adopt", new Identifier(role));
    }

}
