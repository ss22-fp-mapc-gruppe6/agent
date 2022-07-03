package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import g6Agent.services.Point;

public class Survey extends Action implements G6Action {

    /**
     * Localises or gathers information about a target. Moving targets are harder to survey.
     * In this case, the agent will receive a percept with the distance to the nearest dispenser, goal zone or role zone before the next step.
     *
     * @param targetToSurvey The target type. One of "dispenser", "goal", "role"
     */
    public Survey(String targetToSurvey) {
        super("survey", new Identifier(targetToSurvey));
    }

    /**
     * Localises or gathers information about a target. Moving targets are harder to survey.
     * In this case, the agent will receive information about the agent (name, role and energy) at the given location.
     *
     * @param targetToSurvey The x/y coordinates of the target.
     */
    public Survey(Point targetToSurvey) {
        super("survey", new Numeral(targetToSurvey.x), new Numeral(targetToSurvey.y));
    }
}
