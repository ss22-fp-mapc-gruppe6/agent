package g6Agent.perceptionAndMemory.Enties;

import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import g6Agent.agents.MyTestAgent;
import g6Agent.services.Point;

import java.util.HashSet;

public class Block {
    private final String blocktype;
    private  Point coordinates;

    private boolean isAttached = false;




    /**
     * Only Used by Task - Constructor
     * @param p the Function Parameter in a Task Percept
     */
    Block(Function p) {
        this.blocktype = ((Identifier)p.getParameters().get(2)).toProlog();
        this.coordinates= new Point(((Numeral) p.getParameters().get(0)).getValue().intValue(),
                ((Numeral) p.getParameters().get(1)).getValue().intValue());
       // System.out.println(blocktype + " " + coordinates); // TODO find out what points with value 99 mean -1?
    }

    public Block(Point coordinates, String blocktype) {
        this.blocktype = blocktype;
        this.coordinates = coordinates;
    }

    /**
     * Returns the Blocktype of the Requirement
     * @return the Blocktype
     */
    public String getBlocktype() {
        return blocktype;
    }

    public boolean setIsAttached(boolean isAttached) { return this.isAttached = isAttached;}
    public boolean getIsAttached() {return isAttached; }
    /**
     * Returns the relative coordinates to the Agent of the given Block
     * @return the coordinates
     */
    public Point getCoordinates() {
        return coordinates;
    }
    public Point setCoordinates(Point point){
       return this.coordinates= point;
    }
}
