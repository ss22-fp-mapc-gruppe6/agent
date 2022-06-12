package g6Agent.actions;

import g6Agent.agents.MyTestAgent;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;


public interface g6Action  {
        static final String TAG = "Action";

    void setSucceededEffect(MyTestAgent agent, int step, PerceptionAndMemory perception);
    //public static g6Action clear(Point point) {return new Action("clear", new Numeral(point.x), new Numeral(point.y));}
    }

