package g6Agent.perceptionAndMemory;

import g6Agent.services.Point;

/**
 * Internal class of @Link{PerceptionAndMemoryImplementation} to Save AgentPositions with corresponding Teamname
 */
class AgentEntry {
    private String teamName;
    private Point coordinates;

    public AgentEntry(String teamName, Point coordinates) {
        this.teamName = teamName;
        this.coordinates = coordinates;
    }

    public String getTeamName() {
        return teamName;
    }

    public Point getCoordinates() {
        return coordinates;
    }
}
