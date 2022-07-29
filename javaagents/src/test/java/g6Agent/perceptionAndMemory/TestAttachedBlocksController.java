package g6Agent.perceptionAndMemory;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Rotation;

import java.util.List;

public class TestAttachedBlocksController {

    @org.junit.Test
    public void testAttachedBlocksActions() {
        PerceptionAndMemory pam = new PerceptionAndMemoryImplementation();
        Direction d = Direction.EAST;
        List<Percept> pl = List.of(new Percept("thing", new Numeral(d.getNextCoordinate().x), new Numeral(d.getNextCoordinate().y), new Identifier("block"), new Identifier("B1")),
                new Percept("attached", new Numeral(d.getNextCoordinate().x), new Numeral(d.getNextCoordinate().y)));

        //Test attach
        AttachedBlocksModule abc = new AttachedBlocksModule(pam);
        pam.handlePercepts(pl);
        LastActionMemory memory1 = new LastActionMemory();
        memory1.setName("attach");
        memory1.setSuccessfulMessage("success");
        memory1.setLastActionParameters(List.of(new ParameterList(new Identifier("e"))));
        abc.reportLastAction(memory1);
        assert (abc.getAttachedBlocks().size() == 1);

        //Test rotate
        LastActionMemory memory2 = new LastActionMemory();
        memory2.setName("rotate");
        memory2.setSuccessfulMessage("success");
        memory2.setLastActionParameters(List.of(new ParameterList(new Identifier("cw"))));
        abc.reportLastAction(memory2);
        assert (abc.getAttachedBlocks().size() == 1);
        assert (abc.getAttachedBlocks().get(0).getCoordinates().equals(Direction.EAST.rotate(Rotation.CLOCKWISE).getNextCoordinate()));

        //Test detach
        LastActionMemory memory3 = new LastActionMemory();
        memory3.setName("detach");
        memory3.setSuccessfulMessage("success");
        memory3.setLastActionParameters(List.of(new ParameterList(Direction.EAST.rotate(Rotation.CLOCKWISE).getIdentifier())));
        abc.reportLastAction(memory3);
        assert (abc.getAttachedBlocks().isEmpty());
    }
}
