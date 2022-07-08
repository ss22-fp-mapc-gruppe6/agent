package g6Agent.communicationModule;

import g6Agent.communicationModule.submodules.StrategyModule;
import g6Agent.communicationModule.submodules.TaskAuctionModule;
import g6Agent.decisionModule.entities.Strategy;
import org.junit.Test;

public class TestStrategyModule {

    @Test
    public void strategyModuleTest(){
        MailServiceStub mailservice = new MailServiceStub();
        CommunicationModule comMod1 = new CommunicationModuleImplementation("A1", mailservice);
        mailservice.registerCommunicationModule("A1", comMod1);
        StrategyModule sm1 = comMod1.getStrategyModule();
        CommunicationModule comMod2 = new CommunicationModuleImplementation("A2", mailservice);
        mailservice.registerCommunicationModule("A2", comMod2);
        StrategyModule sm2 = comMod2.getStrategyModule();

        sm1.broadcastMyStrategy(Strategy.OFFENSE);
        assert(sm1.getMyStrategy().equals(Strategy.OFFENSE));
        assert (!sm2.getAllStrategies().isEmpty());
        assert (sm2.getAllStrategies().get(0).equals(Strategy.OFFENSE));
        sm2.broadcastMyStrategy(Strategy.DEFENSE);
        System.out.println(sm1.getOffensivePercentage());
        assert (sm1.getAllStrategies().size() == 2);
        assert (sm1.getAllStrategies().stream().anyMatch(s -> s.equals(Strategy.DEFENSE)));
        assert (sm1.getOffensivePercentage() == 0.5);
    }
}
