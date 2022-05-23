package g6Agent.perceptionAndMemory;

import eis.iilang.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.PerceptionAndMemoryImplementation;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.List;

public class TestPerception {

    @org.junit.Test
    public void obstaclePerceptionTest() {
        Percept p = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("obstacle"), new Identifier(""));
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        assert (pam.getObstacles().size() == 0);
        List<Percept> pl = new ArrayList<>();
        pl.add(p);
        pam.handlePercepts(pl);
        assert (pam.getObstacles().size() == 1);
        assert (pam.getObstacles().get(0).equals(new Point(1, 1)));
    }

    @org.junit.Test
    public void testScorePerception() {
        PerceptionAndMemoryImplementation pam = new PerceptionAndMemoryImplementation();
        List<Percept> pl = new ArrayList<>();
        Percept p = new Percept("score", new Numeral(1));
        pl.add(p);
        pam.handlePercepts(pl);
        assert (pam.getScore() == 1);
    }


    @org.junit.Test
    public void testLastActionPerception() {
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        List<Percept> pl = new ArrayList<>();
        Percept p = new Percept("lastAction", new Identifier("move"));
        pl.add(p);
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Identifier("w"));
        Percept p2 = new Percept("lastActionParams", parameters);
        pl.add(p2);
        pl.add(new Percept("lastActionResult", new Identifier("success")));
        assert (pam.getLastAction().getParameters().size() == 0);
        assert (!pam.getLastAction().getSuccessMessage().equals("success"));
        pam.handlePercepts(pl);
        assert (pam.getLastAction().getName().equals("move"));
        assert (pam.getLastAction().getParameters().size() == 1);
        assert (pam.getLastAction().getSuccessMessage().equals("success"));
    }

    @org.junit.Test
    public void testEnergyPerception() {
        PerceptionAndMemoryImplementation pam = new PerceptionAndMemoryImplementation();
        assert (pam.getEnergy() == 100);
        List<Percept> pl = new ArrayList<>();
        Percept p = new Percept("energy", new Numeral(90));
        pl.add(p);
        pam.handlePercepts(pl);
        assert (pam.getEnergy() == 90);
    }

    @org.junit.Test
    public void testNamePerception() {
        PerceptionAndMemoryImplementation pam = new PerceptionAndMemoryImplementation();
        assert (pam.getName() == null);
        List<Percept> pl = new ArrayList<>();
        Percept p = new Percept("name", new Identifier("A1"));
        pl.add(p);
        pam.handlePercepts(pl);
        assert (pam.getName().equals("A1"));
    }

    @org.junit.Test
    public void testReadiness() {
        PerceptionAndMemoryImplementation pam = new PerceptionAndMemoryImplementation();
        List<Percept> pl = new ArrayList<>();
        assert (!pam.isDeactivated());
        assert (!pam.isReadyForAction());
        pl.add(new Percept("deactivated", new Identifier("true")));
        pam.handlePercepts(pl);
        assert (pam.isDeactivated() && !pam.isReadyForAction());
        pl = new ArrayList<>();
        pl.add(new Percept("deactivated", new Identifier("false")));
        pl.add(new Percept("actionID", new Numeral(0)));
        pam.handlePercepts(pl);
        assert (!pam.isDeactivated() && pam.isReadyForAction());
    }

    @org.junit.Test
    public void testTeamAndAgentsPerception() {
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        List<Percept> pl = new ArrayList<>();
        pl.add(new Percept("team", new Identifier("A")));
        pl.add(new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("entity"), new Identifier("A")));
        pl.add(new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("entity"), new Identifier("B")));
        pam.handlePercepts(pl);
        assert (pam.getTeam().equals("A"));
        assert (pam.getFriendlyAgents().size() == 1);
        assert (pam.getEnemyAgents().size() == 1);
    }
    /*
    @org.junit.Test
    public void testTasks() {
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        List<Percept> pl = new ArrayList<>();
        Percept p = (new Percept("task",
                new Identifier("task1"), new Numeral(100), new Numeral(200),
                new ParameterList(new Function("req", new Numeral(1), new Numeral(1), new Identifier("B1")))));
        pl.add(p);
        pam.handlePercepts(pl);
        assert (pam.getTasks().size() == 1);
        assert (pam.getTasks().get(0).getName().equals("task1"));
        assert (pam.getTasks().get(0).getRequirements().get(0).getBlocktype().equals("B1"));
    }
    */
    @org.junit.Test
    public void testMisc() {
        Percept block = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("block"), new Identifier("B1"));
        Percept block2 = new Percept("thing", new Numeral(2), new Numeral(2), new Identifier("dispenser"), new Identifier("B0"));
        Percept marker = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("marker"), new Identifier("ci"));
        Percept steps = new Percept("steps", new Numeral(700));
        Percept step = new Percept("step", new Numeral(100));
        Percept teamsize = new Percept("teamSize", new Numeral(40));
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        List<Percept> pl = new ArrayList<>();
        pl.add(block);
        pl.add(block2);
        pl.add(marker);
        pl.add(steps);
        pl.add(step);
        pl.add(teamsize);
        assert (pam.getSteps() == 0);
        pam.handlePercepts(pl);
        assert (pam.getBlocks().size() == 1);
        assert (pam.getBlocks().get(0).getBlocktype().equals("B1"));
        assert (pam.getBlocks().get(0).getCoordinates().x == 1);
        assert (pam.getBlocks().get(0).getCoordinates().y == 1);
        assert (pam.getMarkers().get(0).iD().equals("ci"));
        assert (pam.getMarkers().get(0).coordinates().x == 1);
        assert (pam.getMarkers().get(0).coordinates().y == 1);
        assert (pam.getDispensers().size() == 1);
        assert (pam.getDispensers().get(0).getBlocktype().equals("B0"));
        assert (pam.getDispensers().get(0).getCoordinates().x == 2);
        assert (pam.getDispensers().get(0).getCoordinates().y == 2);
        assert (pam.getSteps() == 700);
        assert (pam.getCurrentStep() == 100);
        assert (pam.getTeamSize() == 40);
    }

    @org.junit.Test
    public void testZones() {
        Percept block = new Percept("roleZone", new Numeral(1), new Numeral(1));
        Percept block2 = new Percept("goalZone", new Numeral(2), new Numeral(2));

        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        List<Percept> pl = new ArrayList<>();
        pl.add(block);
        pl.add(block2);
        pam.handlePercepts(pl);
        assert (pam.getRoleZones().size() == 1);
        assert (pam.getRoleZones().get(0).x == 1);
        assert (pam.getRoleZones().get(0).y == 1);
        assert (pam.getGoalZones().size() == 1);
        assert (pam.getGoalZones().get(0).x == 2);
        assert (pam.getGoalZones().get(0).y == 2);
    }

    @org.junit.Test
    public void dummyTest() {
        System.out.println("run dummy test to check whether github action caches correctly");
        assert true;
    }

    @org.junit.Test
    public void attachedBlocksTest(){
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        Percept block = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("block"), new Identifier("B1"));
        List<Percept> pl = new ArrayList<>();
        pl.add(block);
        pam.handlePercepts(pl);
        assert(pam.getAttachedBlocks().isEmpty());
        Percept attached = new Percept("attached", new Numeral(1), new Numeral(1));
        pl = new ArrayList<>();
        pl.add(attached);
        pam.handlePercepts(pl);
        assert (pam.getAttachedBlocks().isEmpty());
        pl = new ArrayList<>();
        pl.add(attached);
        pl.add(block);
        pam.handlePercepts(pl);
        assert (!pam.getAttachedBlocks().isEmpty());
    }
}
