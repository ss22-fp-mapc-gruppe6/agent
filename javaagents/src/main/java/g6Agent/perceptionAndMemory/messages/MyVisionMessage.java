package g6Agent.perceptionAndMemory.messages;


import eis.iilang.*;
import g6Agent.MailService;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.Vision;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to Communicate the Vision Of Agents used by SwarmSightController
 *
 * @author Kai Müller
 */
public record MyVisionMessage (Vision vision, String sender) {

    /**
     * Broadcasts the Message over the Mailservice
     * @param mailService the mailservice
     */
    public void broadcast(MailService mailService){
        ParameterList functions = new ParameterList();
        for (Block dispenser : vision.dispensers()) {
            functions.add(new Function("dispenser", new Identifier(dispenser.getBlocktype()), new Numeral(dispenser.getCoordinates().x), new Numeral(dispenser.getCoordinates().y)));
        }
        for (Block block : vision.blocks()) {
            functions.add(new Function("block", new Identifier(block.getBlocktype()), new Numeral(block.getCoordinates().x), new Numeral(block.getCoordinates().y)));
        }
        for (Point roleZone : vision.roleZones()) {
            functions.add(new Function("rolezone", new Numeral(roleZone.getX()), new Numeral(roleZone.getY())));
        }
        for (Point goalZone : vision.goalZones()) {
            functions.add(new Function("goalzone", new Numeral(goalZone.x), new Numeral(goalZone.y)));
        }
        for (Point obstacle : vision.obstacles()) {
            functions.add(new Function("obstacle", new Numeral(obstacle.x), new Numeral(obstacle.y)));
        }
        mailService.broadcast(new Percept("MY_VISION", functions), sender);
    }

    /**
     * Constructor from a Message, null if Name of the Message is not "MY_VISION"
     * @param message the message
     * @param sender the sender
     * @return the new MyVisionMessage
     */
    public static MyVisionMessage fromMail(Percept message, String sender){
        if(!message.getName().equals("MY_VISION")) return null;
        List<Block> dispensers = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();
        List<Point> roleZones = new ArrayList<>();
        List<Point> goalZones = new ArrayList<>();
        List<Point> obstacles = new ArrayList<>();
        //parse Percept and write to lists, save as vision
        for (Parameter parameter : (ParameterList) message.getParameters().get(0)) {
            if (parameter instanceof Function function) {
                switch (function.getName()) {
                    case "dispenser" -> dispensers.add(new Block(
                            new Point(
                                    ((Numeral) function.getParameters().get(1)).getValue().intValue(),
                                    ((Numeral) function.getParameters().get(2)).getValue().intValue()
                            ),
                            ((Identifier) function.getParameters().get(0)).toProlog()
                    ));
                    case "block" -> blocks.add(new Block(
                            new Point(
                                    ((Numeral) function.getParameters().get(1)).getValue().intValue(),
                                    ((Numeral) function.getParameters().get(2)).getValue().intValue()
                            ),
                            ((Identifier) function.getParameters().get(0)).toProlog()
                    ));
                    case "rolezone" -> roleZones.add(new Point(
                            ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                            ((Numeral) function.getParameters().get(1)).getValue().intValue()
                    ));
                    case "goalzone" -> goalZones.add(new Point(
                            ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                            ((Numeral) function.getParameters().get(1)).getValue().intValue()
                    ));
                    case "obstacle" -> obstacles.add(new Point(
                            ((Numeral) function.getParameters().get(0)).getValue().intValue(),
                            ((Numeral) function.getParameters().get(1)).getValue().intValue()
                    ));
                }
            }
        }
        return new MyVisionMessage(new Vision(dispensers,blocks,roleZones,goalZones,obstacles), sender);
    }
}
