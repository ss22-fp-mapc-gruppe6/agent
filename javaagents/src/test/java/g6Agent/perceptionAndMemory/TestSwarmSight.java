package g6Agent.perceptionAndMemory;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import g6Agent.communicationModule.CommunicationModule;
import g6Agent.communicationModule.CommunicationModuleImplementation;
import g6Agent.communicationModule.MailServiceStub;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

public class TestSwarmSight {

    @Test
    public void swarmSightTest(){
        MailServiceStub ms = new MailServiceStub();
        PerceptionAndMemoryImplementation pam1 = new PerceptionAndMemoryImplementation();
        SwarmSightController ssc1 = new SwarmSightController(ms, pam1, pam1, "A1");
        pam1.addLastActionListener(ssc1);
        pam1.setVisionReporter(ssc1);
        CommunicationModule com1 = new CommunicationModuleImplementation("A1", ms);
        com1.addSwarmSightController(ssc1);
        ms.registerCommunicationModule("A1", com1);

        PerceptionAndMemoryImplementation pam2 = new PerceptionAndMemoryImplementation();
        SwarmSightController ssc2 = new SwarmSightController(ms, pam2, pam2, "A2");
        pam2.addLastActionListener(ssc2);
        pam2.setVisionReporter(ssc2);
        CommunicationModule com2 = new CommunicationModuleImplementation("A2", ms);
        com2.addSwarmSightController(ssc2);
        ms.registerCommunicationModule("A2", com2);

        Percept teamPercept = new Percept("team", new Identifier("A"));
        Percept agentCoord1 = new Percept("thing", new Numeral(1), new Numeral(1), new Identifier("entity"), new Identifier("A"));
        Percept agentCoord2 = new Percept("thing", new Numeral(-1), new Numeral(-1), new Identifier("entity"), new Identifier("A"));
        Percept role = new Percept("role",
                new Identifier("id") ,
                new Numeral(5),
                new ParameterList(new Identifier("move")),
                new ParameterList(new Numeral(2)),
                new Numeral(1.0),
                new Numeral(1));
        Percept roleID = new Percept("role", new Identifier("id"));
        Percept step = new Percept("step", new Numeral(2));

        pam1.handlePercepts(List.of(teamPercept, agentCoord1, role, roleID, step));
        pam2.handlePercepts(List.of(teamPercept, agentCoord2, role, roleID, step));
        assert (pam1.getPositionOfKnownAgent("A2") == null);
        assert (pam2.getPositionOfKnownAgent("A1") == null);
        assert (pam2.getPositionOfKnownAgent("A2") == null);

        pam1.initiateSync();
        pam2.initiateSync();
        assert (pam1.getPositionOfKnownAgent("A2") == null);
        assert (pam2.getPositionOfKnownAgent("A1") == null);
        assert (pam2.getPositionOfKnownAgent("A2") == null);

        pam1.handleSyncRequests();
        pam2.handleSyncRequests();
        assert (pam1.getPositionOfKnownAgent("A2") == null);
        assert (pam2.getPositionOfKnownAgent("A1") == null);
        assert (pam2.getPositionOfKnownAgent("A2") == null);


        pam1.finishSync();
        pam2.finishSync();
        assert (!pam1.getKnownAgents().isEmpty());
        assert (pam1.getPositionOfKnownAgent("A2") != null);
        for (var agent : pam1.getKnownAgents()){
            System.out.println(agent.name() + " : " + agent.position());
        }
        assert (pam2.getPositionOfKnownAgent("A1").equals(new Point(-1,-1)));
        assert (pam2.getPositionOfKnownAgent("A2") == null);


        Percept lastActionName = new Percept("lastAction", new Identifier("move"));
        Percept lastActionParameters = new Percept("lastActionParams", new ParameterList(new Identifier("w")));
        Percept lastActionSuccess = new Percept("lastActionResult", new Identifier("success"));
        Percept agentCoord1updated = new Percept("thing", new Numeral(1+ Direction.WEST.getNextCoordinate().x), new Numeral(1+Direction.WEST.getNextCoordinate().y), new Identifier("entity"), new Identifier("A"));
        Percept agentCoord2updated = new Percept("thing", new Numeral(-1- Direction.WEST.getNextCoordinate().x), new Numeral(-1-Direction.WEST.getNextCoordinate().y), new Identifier("entity"), new Identifier("A"));

        pam1.handlePercepts(List.of(teamPercept,
               //agentCoord1updated,
                role, roleID, step));
        pam2.handlePercepts(List.of(teamPercept,
                //agentCoord2updated,
                role, roleID, step));

        pam1.handlePercepts(List.of(teamPercept, agentCoord1updated, role, roleID, step, lastActionName, lastActionParameters, lastActionSuccess));
        pam2.handlePercepts(List.of(teamPercept, agentCoord2updated, role, roleID, step));
        //pam1.initiateSync();
        //pam2.initiateSync();
        for (var agent : pam1.getKnownAgents()){
            System.out.println(agent.name() + " : " + agent.position());
        }
        for (var agent : pam2.getKnownAgents()){
            System.out.println(agent.name() + " : " + agent.position());
        }
        System.out.println(new Point(-1,-1).add((Direction.WEST.getNextCoordinate().invert()).multiply(2)));
        assert (pam2.getPositionOfKnownAgent("A1").equals(new Point(-1,-1).add(Direction.WEST.getNextCoordinate().multiply(2))));
     }

     @Test
    public void hashSetTest(){
         HashSet<Block> blocks = new HashSet<>();
         blocks.add(new Block( new Point(1,1), "B1"));
         blocks.add(new Block( new Point(1,1), "B1"));
         assert (blocks.size() == 1);
         HashSet<Point> points = new HashSet<>();
         points.add(new Point(1,1));
         points.add(new Point(1,1));
         assert (points.size() == 1);
    }
}
