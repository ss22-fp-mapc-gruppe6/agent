package g6Agent.communicationModule.submodules;

import eis.iilang.Identifier;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.decisionModule.entities.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StrategyModule {

    private final String agentName;
    private final HashMap<String, Strategy> strategies;
    private final MailService mailservice;


    public StrategyModule(String agentName, MailService mailservice) {
        this.agentName = agentName;
        this.mailservice = mailservice;
        this.strategies = new HashMap<>();
    }

    public void broadcastMyStrategy(Strategy strategy){
        strategies.put(agentName, strategy);
        mailservice.broadcast(new Percept("MY_STRATEGY", new Identifier(strategy.name())), agentName);
    }

    public void receiveStrategyUpdate(Percept message, String sender){
        if(message.getName().equals("MY_STRATEGY") && message.getParameters().size() == 1) {
            Strategy strategy = Strategy.fromIdentifier((Identifier) message.getParameters().get(0));
            strategies.put(sender, strategy);
        }
    }

    public Strategy getMyStrategy() {
        return strategies.get(agentName);
    }

    public List<Strategy> getAllStrategies() {
        List<Strategy> strategyList= new ArrayList<>();
        strategies.forEach((agent, strategy) -> strategyList.add(strategy));
        return strategyList;
    }

    public double getOffensivePercentage() {
        List<Strategy> offensiveList = new ArrayList<>();
        List<Strategy> defensiveList = new ArrayList<>();
        strategies.forEach((agent, strategy) ->{
            if (strategy.equals(Strategy.OFFENSE)) offensiveList.add(strategy);
            else defensiveList.add(strategy);
        }  );
        int offensive = offensiveList.size();
        int defensive = defensiveList.size();

        if (defensive + offensive == 0) return 1.0; // no zero division

        return (double) offensive / (double) (defensive + offensive);
    }
}
