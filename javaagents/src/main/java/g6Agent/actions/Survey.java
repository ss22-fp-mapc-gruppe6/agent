package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Survey extends Action implements G6Action {
    private Point pointToSurvey;
    private String stringToSurvey;

    /**
     * Localises or gathers information about a target. Moving targets are harder to survey.
     * In this case, the agent will receive a percept with the distance to the nearest dispenser, goal zone or role zone before the next step.
     *
     * @param targetToSurvey The target type. One of "dispenser", "goal", "role"
     */
    public Survey(String targetToSurvey) {
        super("survey", new Identifier(targetToSurvey));
        stringToSurvey = targetToSurvey;
    }

    /**
     * Localises or gathers information about a target. Moving targets are harder to survey.
     * In this case, the agent will receive information about the agent (name, role and energy) at the given location.
     *
     * @param targetToSurvey The x/y coordinates of the target.
     */

    public Survey(Point targetToSurvey) {
        super("survey", new Numeral(targetToSurvey.x), new Numeral(targetToSurvey.y));
        this.pointToSurvey = targetToSurvey;
    }
    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) {
        if (stringToSurvey != null) {
            return stringToSurvey.equals("dispenser") || stringToSurvey.equals("goal") || stringToSurvey.equals("role");
        } else {
            boolean isFriendlyAgent = perceptionAndMemory.getFriendlyAgents().stream().anyMatch((x -> x.equals(pointToSurvey)));
            boolean isEnemyAgent = perceptionAndMemory.getEnemyAgents().stream().anyMatch(x -> x.equals(pointToSurvey));
            return isFriendlyAgent || isEnemyAgent;
        }
    }

}
