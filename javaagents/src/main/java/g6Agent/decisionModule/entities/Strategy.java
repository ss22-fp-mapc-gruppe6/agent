package g6Agent.decisionModule.entities;

import eis.iilang.Identifier;

public enum Strategy {
    OFFENSE("worker"), DEFENSE("digger");


    private final String preferredRoleName;

    Strategy(String preferredRoleName) {
        this.preferredRoleName = preferredRoleName;
    }

    public static Strategy fromIdentifier(Identifier identifier) {
        if (identifier.toProlog().equals(OFFENSE.toString())){
            return Strategy.OFFENSE;
        } else {
            return Strategy.DEFENSE;
        }
    }

    public String getPreferredRoleName() {
        return preferredRoleName;
    }
}
