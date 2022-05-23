package g6Agent.decisionModule;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import g6Agent.decissionModule.DecisionModule;
import g6Agent.decissionModule.TheStupidestDecisionModule;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.List;

public class TestTheStupidestDecisionModule {

    @org.junit.Test
    public void testTheStupidestDecisionModule() {
        /*
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        DecisionModule dm = new TheStupidestDecisionModule(pam);
        assert (dm.revalidateGoal().getName().equals("G6GoalExplore"));
        List<Parameter> l = new ArrayList<>();
        l.add(new Numeral(0));
        l.add(new Numeral(1));
        Percept p = new Percept("attached", l);
        List<Percept> pl = new ArrayList<>();
        pl.add(p);
        pam.handlePercepts(pl);
        assert (pam.getAttached().size() > 0);
        assert (dm.revalidateGoal().getName().equals("G6GoalGoalRush"));

        Percept dis = new Percept("thing", new Numeral(2), new Numeral(2), new Identifier("dispenser"), new Identifier("B0"));
        pl = new ArrayList<>();
        pl.add(dis);
        pam.handlePercepts(pl);
        assert (dm.revalidateGoal().getName().equals("G6GoalRetrieveBlock"));


        Percept o = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("obstacle"), new Identifier(""));
        Percept o2 = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("obstacle"), new Identifier(""));
        Percept o3 = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("obstacle"), new Identifier(""));
        Percept o4 = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("obstacle"), new Identifier(""));
        Percept o5 = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("obstacle"), new Identifier(""));
        Percept o6 = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("obstacle"), new Identifier(""));
        pl = new ArrayList<>();
        pl.add(o);
        pl.add(o2);
        pl.add(o3);
        pl.add(o4);
        pl.add(o5);
        pl.add(o6);
        pam.handlePercepts(pl);
        assert (dm.revalidateGoal().getName().equals("G6GoalDig"));
        */
    }
}
