package g6Agent.perceptionAndMemory.messages;

import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import g6Agent.MailService;

/**
 * Message to communicate a movement by an Agent
 * @param messageCounter
 * @param currentStep
 * @param movementParameter
 * @param speed
 * @param sender
 *
 * @author Kai Müller
 */

public record MovementNotificationMessage(int messageCounter, int currentStep, Parameter movementParameter,
                                          int speed, String sender) {

    public static MovementNotificationMessage fromMail(Percept message, String sender) {
        if (!message.getName().equals("MOVEMENT_NOTIFICATION")) return null;
        return new MovementNotificationMessage(
                ((Numeral) message.getParameters().get(0)).getValue().intValue(),
                 ((Numeral) message.getParameters().get(1)).getValue().intValue(),
                 message.getParameters().get(2),                                                 //directions
                ((Numeral) message.getParameters().get(3)).getValue().intValue(),                //speed
                sender);
    }

    public void broadcast(MailService mailService){
        mailService.broadcast(new Percept("MOVEMENT_NOTIFICATION",
                new Numeral(messageCounter),
                new Numeral(currentStep),
                movementParameter,
                new Numeral(speed)
        ), sender);
    }

}
