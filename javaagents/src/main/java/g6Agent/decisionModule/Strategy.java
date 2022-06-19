package g6Agent.decisionModule;

public enum Strategy {
    OFFENSE("worker"), DEFENSE("digger");


    private final String preferredRoleName;

    Strategy(String preferredRoleName) {
        this.preferredRoleName = preferredRoleName;
    }

    public String getPreferredRoleName() {
        return preferredRoleName;
    }
}
