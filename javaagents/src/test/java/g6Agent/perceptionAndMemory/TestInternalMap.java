package g6Agent.perceptionAndMemory;


import g6Agent.perceptionAndMemory.Enties.Movement;
import g6Agent.services.Direction;
import g6Agent.services.Point;

public class TestInternalMap {

    @org.junit.Test
    public void internalMapTest() {
        InternalMapOfOtherAgents map = new InternalMapOfOtherAgents("");
        InternalMapEntry pos = map.getAgentPosition("A1");
        assert (pos == null);
        //tests for Spotting Agents
        map.spottetAgent("A1", new Point(1,1));
        pos = map.getAgentPosition("A1");
        assert (pos.getPosition().equals(new Point(1,1)));
        //tests for increasing counters
        int oldcounter = pos.getCounter();
        map.incrementAllCounters();
        assert(oldcounter +1 == pos.getCounter());
        //tests for spotting Agents
        InternalMapEntry entryFromOtherAgent = new InternalMapEntry(new Point(2,2));
        map.spottetAgent("A2", new Point(2,2));
        assert (map.isKnown("A2"));
        map.heardOfAgentPosition("A1", entryFromOtherAgent, "A3");
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,1)));
        map.heardOfAgentPosition("A1", new InternalMapEntry(new Point(2,2), 3), "A2");
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,1)));
        map.incrementAllCounters();
        map.heardOfAgentPosition("A1", new InternalMapEntry(new Point(2,2), 0), "A2");
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(4,4)));
    }

    @org.junit.Test
    public void movementTest(){
        InternalMapOfOtherAgents map = new InternalMapOfOtherAgents("");
        map.spottetAgent("A1", new Point(1,1));
        map.movedMyself(new Movement(Direction.EAST, 1));
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1, 1).add(Direction.EAST.getNextCoordinate().invert())));
        map = new InternalMapOfOtherAgents("");
        map.spottetAgent("A1", new Point(1,1));
        map.movedMyself(new Movement(Direction.WEST, 1));
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,1).add(Direction.WEST.getNextCoordinate().invert())));
        map = new InternalMapOfOtherAgents("");
        map.spottetAgent("A1", new Point(1,1));
        map.movedMyself(new Movement(Direction.SOUTH, 1));
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,1).add(Direction.SOUTH.getNextCoordinate().invert())));
        map = new InternalMapOfOtherAgents("");
        map.spottetAgent("A1", new Point(1,1));
        map.movedMyself(new Movement(Direction.NORTH, 1));
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,1).add(Direction.NORTH.getNextCoordinate().invert())));
    }

    @org.junit.Test
    public void movementNotificationTest(){

        InternalMapOfOtherAgents map = new InternalMapOfOtherAgents("");
        map.spottetAgent("A1", new Point(1,1));
        map.notifiedOfMovement("A1", new Movement(Direction.WEST, 1));
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,1).add(Direction.WEST.getNextCoordinate())));
        /*
        map = new InternalMapOfOtherAgents(new BasicAgent("", new MailService()));
        map.spottetAgent("A1", new Point(1,1));
        map.notifiedOfMovement("A1", Direction.EAST);
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(2,1)));
        map = new InternalMapOfOtherAgents(new BasicAgent("", new MailService()));
        map.spottetAgent("A1", new Point(1,1));
        map.notifiedOfMovement("A1", Direction.NORTH);
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,2)));
        map = new InternalMapOfOtherAgents(new BasicAgent("", new MailService()));
        map.spottetAgent("A1", new Point(1,1));
        map.notifiedOfMovement("A1", Direction.SOUTH);
        assert (map.getAgentPosition("A1").getPosition().equals(new Point(1,0)));

         */
    }

}
