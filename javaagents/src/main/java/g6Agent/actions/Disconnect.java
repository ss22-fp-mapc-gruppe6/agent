package g6Agent.actions;

import eis.iilang.Action;
import eis.iilang.Numeral;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

public class Disconnect extends Action implements G6Action {
    /**
     * Disconnects two attachments (probably blocks) of the agent.
     *
     * @param attachment1 The x/y coordinates of the first attachment.
     * @param attachment2 The x/y coordinates of the second attachment.
     */
    private Point attachment1;
    private Point attachment2;
    public Disconnect(Point attachment1, Point attachment2) {
        super("disconnect", new Numeral(attachment1.x), new Numeral(attachment1.y), new Numeral(attachment2.x), new Numeral(attachment2.y));
       this.attachment1 = attachment1;
       this.attachment2 = attachment2;
    }
    @Override
    public boolean predictSuccess(PerceptionAndMemory perceptionAndMemory) throws Exception {
        if (perceptionAndMemory.getCurrentRole() == null) return false;
        int maxDistance = perceptionAndMemory.getCurrentRole().getClearActionMaximumDistance();
        if (attachment1.manhattanDistanceTo(new Point(0,0)) > maxDistance) return false;
        if (attachment2.manhattanDistanceTo(new Point(0,0)) > maxDistance) return false;

        boolean isAttachment1 = perceptionAndMemory.getAttachedBlocks().stream().anyMatch(x -> x.equals(attachment1));
        boolean isAttachment2 = perceptionAndMemory.getAttachedBlocks().stream().anyMatch(x -> x.equals(attachment2));

        return (isAttachment1 || isAttachment2 );
    }

}
