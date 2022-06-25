package g6Agent.communicationModule;

import g6Agent.MailService;
import g6Agent.communicationModule.entities.SubTaskWithCost;
import g6Agent.communicationModule.submodules.TaskAuctionModule;
import org.junit.Test;

public class TestTaskAuctioningModule {

    @Test
    public void testAuction(){
        MailServiceStub mailservice = new MailServiceStub();
        CommunicationModule comMod1 = new CommunicationModuleImplementation("A1", mailservice);
        mailservice.registerCommunicationModule("A1", comMod1);
        TaskAuctionModule tam1 = comMod1.getTaskAuctionModule();

        CommunicationModule comMod2 = new CommunicationModuleImplementation("A2", mailservice);
        mailservice.registerCommunicationModule("A2", comMod2);
        TaskAuctionModule tam2 = comMod2.getTaskAuctionModule();

        tam1.acceptTask(new SubTaskWithCost("t1", 1, 3));

        assert (tam1.getMySubTask().taskname().equals("t1"));
        assert (tam2.getMySubTask() == null);
        assert (tam1.getAgentsAtSameTask().isEmpty());
        assert (!tam2.getEntries().isEmpty());
        assert (tam2.getEntries().stream().anyMatch(entry -> entry.agentname().equals("A1")));

        tam2.acceptTask(new SubTaskWithCost("t1", 1, 2));
        assert (tam2.getMySubTask().taskname().equals("t1"));
        assert (tam1.getMySubTask() == null);
        assert (tam2.getAgentsAtSameTask().isEmpty());

        tam1.acceptTask(new SubTaskWithCost("t1", 2, 3));
        assert (tam1.getMySubTask().taskname().equals("t1"));
        assert (tam2.getMySubTask().taskname().equals("t1"));
        assert (!tam2.getAgentsAtSameTask().isEmpty());
    }


}
