package g6Agent.actions;

import g6Agent.agents.MyTestAgent;


public interface g6Action  {
        static final String TAG = "Action";

    void setSucceededEffect(MyTestAgent agent, int step);
    //public static g6Action clear(Point point) {return new Action("clear", new Numeral(point.x), new Numeral(point.y));}
    }

