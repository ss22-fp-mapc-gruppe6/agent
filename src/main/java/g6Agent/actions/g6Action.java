package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Numeral;
import g6Agent.agents.MyTestAgent;
import g6Agent.services.ActionResult;
import g6Agent.services.Direction;
import g6Agent.services.Point;


public interface g6Action  {
        static final String TAG = "Action";


        public abstract void getAgentActionFeedback(ActionResult lastActionResult, MyTestAgent Agent, int step);

        public abstract void succeededEffect(MyTestAgent agent, int step);
        //public static g6Action clear(Point point) {return new Action("clear", new Numeral(point.x), new Numeral(point.y));}
    }

