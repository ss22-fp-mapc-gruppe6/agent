package g6Agent.perceptionAndMemory.messages;

import eis.iilang.Numeral;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.services.Point;

/**
 * An request for if a agent is seen. Used by SwarmSightController
 *
 * @param clock    the clock
 * @param step     the step
 * @param position the inverted position from the sender. is the position the reciever should see the sender at.
 * @param sender   name of the sender
 *
 * @author Kai Müller
 */
public record IntroductionRequest(int clock, int step, Point position, String sender) {


    public void broadcast(MailService mailservice) {
        mailservice.broadcast(new Percept("INTRODUCTION_REQUEST",
                new Numeral(this.clock), new Numeral(this.step), new Numeral(this.position.x), new Numeral(this.position.y)
        ), this.sender);
    }

    public static IntroductionRequest fromMail(Percept percept, String sender) {
        return new IntroductionRequest(
                ((Numeral) percept.getParameters().get(0)).getValue().intValue(), //clock
                ((Numeral) percept.getParameters().get(1)).getValue().intValue(), //step
                new Point(
                        ((Numeral) percept.getParameters().get(2)).getValue().intValue(),
                        ((Numeral) percept.getParameters().get(3)).getValue().intValue()
                ),      //position
                sender //sender
        );
    }


    public void sendAccept(MailService mailService, String sender) {
        mailService.sendMessage(
                new Percept(
                        "INTRODUCTION_ACCEPT",
                        new Numeral(this.clock), new Numeral(this.step), new Numeral(this.position.x), new Numeral(this.position.y)
                ),
                this.sender,
                sender
        );
    }


}