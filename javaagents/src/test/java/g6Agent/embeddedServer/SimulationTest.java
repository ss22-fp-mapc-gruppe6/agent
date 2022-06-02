package g6Agent.embeddedServer;

import eis.iilang.Action;
import eis.iilang.Function;
import eis.iilang.IILElement;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import g6Agent.MailService;
import g6Agent.agents.Agent;
import g6Agent.agents.Agent006;
import massim.ReplayWriter;
import massim.Server;
import massim.config.TeamConfig;
import massim.game.Simulation;
import massim.monitor.Monitor;
import massim.protocol.messages.ActionMessage;
import massim.protocol.messages.Message;
import massim.protocol.messages.RequestActionMessage;
import massim.protocol.messages.scenario.StepPercept;
import massim.util.IOUtil;
import massim.util.Log;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SimulationTest {

    private Simulation sim;

    record Tuple(String name, Action action) {
    }

    @Test
    public void name() throws IOException, ExecutionException, InterruptedException {
        IILElement.toProlog = true;

        final var sampleConfigJson = IOUtil.readJSONObjectWithImport("../server/conf/SampleConfig.json");
        final var replayPath = sampleConfigJson.getJSONObject("server").getString("replayPath");
        ReplayWriter replayWriter = null;
        if (replayPath != null) {
            replayWriter = new ReplayWriter(replayPath);
        }
        final var sim1Json = IOUtil.readJSONObjectWithImport("../server/conf/sim/sim1.json");

        sim1Json.getJSONObject("entities").put("standard", 2);

        var config = Server.parseServerConfig(sampleConfigJson);
        sim = new Simulation();
        Set<TeamConfig> matchTeams = new HashSet<>();
        for (int index : IntStream.rangeClosed(0, config.teamsPerMatch - 1).toArray())
            matchTeams.add(config.teams.get(index));
        int steps = sim1Json.getInt("steps");
        var initialPercepts = sim.init(steps, sim1Json, matchTeams);

        final var monitor = new Monitor(8000);
        final var agents = new HashMap<String, Agent>();
        final var mailService = new MailService();
        agents.put("agentA1", new Agent006("agentA1", mailService));
        agents.put("agentA2", new Agent006("agentA2", mailService));
        agents.put("agentB1", new Agent006("agentB1", mailService));
        agents.put("agentB2", new Agent006("agentB2", mailService));

        for (int i = 0; i < steps; i++) {
            Log.log(Log.Level.NORMAL, "Simulation at step " + i);
            //no server inputs
//            handleInputs(sim);
            var percepts = sim.preStep(i);
//            var actions = agentManager.requestActions(percepts);
            final var actions = percepts.entrySet().stream()
                    .map(entry ->
                         {
                             final var agentId = entry.getKey();
                             final var agent = ((Agent006) agents.get(agentId));
                             final var request = entry.getValue();
                             final var requestJson = request.toJson();
                             final var requestFromJson = Message.buildFromJson(requestJson);
                             final var requestActionPercepts = new ArrayList<>(List.of(new Percept("requestAction")));
                             requestActionPercepts.addAll(requestActionToIIL((RequestActionMessage) requestFromJson));
                             final var action = agent.step(requestActionPercepts);

                             return new Tuple(agentId, action);
                         }

                    ).collect(Collectors.toMap(
                            t -> t.name(),
                            t -> ((ActionMessage) Message.buildFromJson(actionToJSON(0, t.action)))));
//            Map<String, ActionMessage> actions = Map.of();
            sim.step(i, actions); // execute step with agent actions

            Thread.sleep(100);
            //no handle Simstate
//            handleSimState(sim.getName(), startTime, sim.getSnapshot());
            var startTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            handleSimState(sim.getName(), startTime,sim.getStaticData(), monitor, replayWriter);
            handleSimState(sim.getName(), startTime,sim.getSnapshot(), monitor, replayWriter);
        }
        var finalPercepts = sim.finish();
        final var result = sim.getResult();


        System.out.println("simulation = " + sim);

    }

    private void handleSimState(String simId, String startTime, JSONObject world, Monitor monitor, ReplayWriter replayWriter) {
        if (monitor != null) monitor.updateState(world);
        if (replayWriter != null) replayWriter.updateState(simId, startTime, world);
    }


    public JSONObject actionToJSON(long actionID, Action action) {

        // translate parameters to String
        List<String> parameters = new Vector<>();
        action.getParameters().forEach(param -> {
            if (param instanceof Identifier) {
                parameters.add(((Identifier) param).getValue());
            } else if (param instanceof Numeral) {
                parameters.add(((Numeral) param).getValue().toString());
            } else {
                System.out.println("Cannot translate parameter " + param);
                parameters.add(""); // add empty parameter so the order is not invalidated
            }
        });

        // create massim protocol action
        ActionMessage msg = new ActionMessage(action.getName(), actionID, parameters);
        return msg.toJson();
    }

    protected Collection<Percept> requestActionToIIL(RequestActionMessage message) {
        var ret = new HashSet<Percept>();
        if (!(message instanceof StepPercept percept)) return ret; // percept incompatible with entity

        ret.add(new Percept("actionID", num(percept.getId())));
        ret.add(new Percept("timestamp", num(percept.getTime())));
        ret.add(new Percept("deadline", num(percept.getDeadline())));

        ret.add(new Percept("step", num(percept.getStep())));

        ret.add(new Percept("lastAction", id(percept.lastAction)));
        ret.add(new Percept("lastActionResult", id(percept.lastActionResult)));
        var params = new ParameterList();
        percept.lastActionParams.forEach(p -> params.add(id(p)));
        ret.add(new Percept("lastActionParams", params));
        ret.add(new Percept("score", num(percept.score)));

        percept.things.forEach(thing -> ret.add(new Percept("thing",
                                                            num(thing.x), num(thing.y), id(thing.type), id(thing.details))));

        percept.taskInfo.forEach(task -> {
            var reqs = new ParameterList();
            for (var req : task.requirements) {
                reqs.add(new Function("req", num(req.x), num(req.y),
                                      id(req.type)));
            }
            ret.add(new Percept("task", id(task.name), num(task.deadline), num(task.reward), reqs));
        });

        percept.attachedThings.forEach(pos -> ret.add(
                new Percept("attached", num(pos.x), num(pos.y))));

        ret.add(new Percept("energy", num(percept.energy)));
        ret.add(new Percept("deactivated", id(percept.deactivated ? "true" : "false")));
        ret.add(new Percept("role", id(percept.role)));

        percept.violations.forEach(violation ->
                                           ret.add(new Percept("violation", id(violation)))
        );

        percept.normsInfo.forEach(norm -> {
                                      var requirements = new ParameterList();
                                      norm.requirements.forEach(req -> {
                                          var details = req.details;
                                          if (details == null)
                                              details = "";
                                          var subject = new Function("requirement", id(req.type.toString()), id(req.name),
                                                                     num(req.quantity), id(details));
                                          requirements.add(subject);
                                      });
                                      ret.add(new Percept("norm", id(norm.name), num(norm.start),
                                                          num(norm.until), requirements, num(norm.punishment)));
                                  }
        );

        percept.roleZones.forEach(r -> ret.add(new Percept("roleZone", num(r.x), num(r.y))));
        percept.goalZones.forEach(r -> ret.add(new Percept("goalZone", num(r.x), num(r.y))));

        var events = percept.stepEvents;
        for (int i = 0; i < events.length(); i++) {
            var event = events.getJSONObject(i);
            var type = event.getString("type");
            switch (type) {
                case "surveyed" -> {
                    var target = event.getString("target");
                    if (target.equals("agent"))
                        ret.add(new Percept(type, id(target),
                                            id(event.getString("name")),
                                            id(event.getString("role")),
                                            num(event.getInt("energy"))));
                    else
                        ret.add(new Percept(type, id(target), num(event.getInt("distance"))));
                }
                case "hit" -> {
                    var originPos = event.getJSONArray("origin");
                    ret.add(new Percept(type, num(originPos.getInt(0)), num(originPos.getInt(1))));
                }
            }
        }

        return ret;
    }

    private static Identifier id(String value) {
        return new Identifier(value);
    }

    private static Numeral num(Number value) {
        return new Numeral(value);
    }
}
