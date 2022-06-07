package g6Agent.perceptionAndMemory;

import eis.iilang.*;
import g6Agent.ourPercepts.*;
import g6Agent.perceptionAndMemory.Enties.Task;
import g6Agent.perceptionAndMemory.Interfaces.AgentVisionReporter;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;
import g6Agent.perceptionAndMemory.Enties.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import g6Agent.environment.GridObject;

public class PerceptionAndMemoryImplementation implements PerceptionAndMemory {

    private LastActionMemory lastAction;
    private List<Point> obstacles;

    private GridObject listOfAllObstacles;

    private int lastID;
    private boolean isActionIdCheckedSuccessfully;
    private int score;
    private int energy;
    private String name;
    private boolean isDeactivated;
    private int currentId;
    private String team;
    private List<AgentEntry> perceivedAgents;
    private List<Task> tasks;
    private List<Block> blocks;
    private List<Block> dispensers;
    private List<Point> roleZones;
    private List<Point> goalZones;
    private int steps;
    private List<Norm> norms;
    private String currentRoleName;
    private final HashMap<String, Role> possibleRoles;
    private int currentStep;
    private int teamSize;
    private List<Marker> markers;
    private List<Point> attached;

    private final List<LastActionListener> lastActionListeners;
    private AgentVisionReporter visionReporter;

    private record AgentEntry(String team, Point coordinate) {
    }

    public PerceptionAndMemoryImplementation() {
        obstacles = new ArrayList<>();
        lastID = -1;
        currentId = -1;
        isActionIdCheckedSuccessfully = false;
        score = 0;
        energy = 100;
        isDeactivated = false;
        team = "";
        perceivedAgents = new ArrayList<>();
        tasks = new ArrayList<>();
        dispensers = new ArrayList<>();
        roleZones = new ArrayList<>();
        goalZones = new ArrayList<>();
        norms = new ArrayList<>();
        steps = 0;
        currentStep = 0;
        teamSize = 0;
        possibleRoles = new HashMap<>();
        listOfAllObstacles = new GridObject();
        this.lastAction = new LastActionMemory();
        this.markers = new ArrayList<>();
        this.attached = new ArrayList<>();
        this.lastActionListeners = new ArrayList<>(1);
    }

    void setVisionReporter(AgentVisionReporter reporter) {
        this.visionReporter = reporter;
    }

    @Override
    public boolean isDeactivated() {
        return isDeactivated;
    }

    @Override
    public void handlePercepts(List<Percept> perceptInput) {
        clearShortTermMemory();
        final ArrayList<Object> objects = new ArrayList<>();
        for (Percept percept : perceptInput) {
            final List<Parameter> p = percept.getParameters();
            objects.add(OurPerceptUtil.translate(percept));
        }
        if (!objects.isEmpty())
            handleOurPercepts(objects);
        else if (!perceptInput.isEmpty()) {
            clearShortTermMemory();
            try {
                for (Percept percept : perceptInput) {
                    if (percept.getName().equals("actionID")) {
                    } else if (percept.getName().equals("thing")) {
                        handleThingPercept(percept);
                    } else if (percept.getName().equals("score")) {
                        handleScorePercept(percept);
                    } else if (percept.getName().equals("lastAction")) {
                        handleLastAction(percept);
                    } else if (percept.getName().equals("energy")) {
                        handleEnergy(percept);
                    } else if (percept.getName().equals("name")) {
                        handleName(percept);
                    } else if (percept.getName().equals("lastActionParams")) {
                        handleLastActionParams(percept);
                    } else if (percept.getName().equals("deactivated")) {
                        handleDeactivated(percept);
                    } else if (percept.getName().equals("team")) {
                        handleTeam(percept);
                    } else if (percept.getName().equals("lastActionResult")) {
                        handleLastActionResult(percept);
                    } else if (percept.getName().equals("task")) {
                        handleTask(percept);
                    } else if (percept.getName().equals("goalZone")) {
                        handleGoalZone(percept);
                    } else if (percept.getName().equals("roleZone")) {
                        handleRoleZone(percept);
                    } else if (percept.getName().equals("norm")) {
                        handleNorm(percept);
                    } else if (percept.getName().equals("steps")) {
                        this.steps = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
                    } else if (percept.getName().equals("role")) {
                        handleRolePercept(percept);
                    } else if (percept.getName().equals("step")) {
                        this.currentStep = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
                    } else if (percept.getName().equals("teamSize")) {
                        this.teamSize = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
                    } else if (percept.getName().equals("attached")) {
                        this.attached.add(new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue()));
                    } else if (percept.getName().equals("simEnd")) {
                        lastID = -1;
                        currentId = -1;
                        System.out.println("NEW GAME");
                    }
                    //ignore cases
                    else if (!(percept.getName().equals("simStart")
                            || percept.getName().equals("requestAction")
                            || percept.getName().equals("deadline")
                            || percept.getName().equals("timestamp"))) {
                        System.out.println("UNHANDLED PERCEPT : " + percept);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            notifyListenersOfLastAction();
            if (visionReporter != null) {
                visionReporter.reportMyVision(dispensers, roleZones, goalZones, obstacles);
                visionReporter.updateMyVisionWithSightingsOfOtherAgents();
            }
        }
    }

    private void handleOurPercepts(ArrayList<Object> objects) {
        for (Object object : objects) {
            switch (object) {
                case ActionID actionID -> handleActionID(actionID);
                case Attached attached -> handleAttached(attached);
                case Deactivated deactivated -> handleDeactivated(deactivated);
                case Energy energy -> handleEnergy(energy);
                case GoalZone goalZone -> handleGoalZone(goalZone);
                case LastAction lastAction -> handleLastAction(lastAction);
                case LastActionParameters lap -> handleLastActionParameters(lap);
                case LastActionResult lar -> handleLastActionResult(lar);
                case Name name -> handleName(name);
                case Norm norm -> handleNorm(norm);
                case Role role -> handleRole(role);
                case RoleZone roleZone -> handleRoleZone(roleZone);
                case YourRole yourRole -> handleYourRole(yourRole);
                case Score score -> handleScore(score);
                case Step step -> handleStep(step);
                case Steps steps -> handleSteps(steps);
                case Task task -> handleTask(task);
                case Team team -> handleTeam(team);
                case TeamSize teamSize -> handleTeamSize(teamSize);
                case g6Agent.ourPercepts.Thing thing -> handleThing(thing);
                case Ranking ranking -> {
                }
                case SimStart simStart -> {
                }
                case Timestamp timestamp -> {
                }
                case Deadline deadline -> {
                }
                case RequestAction requestAction -> {
                }
                case Violation violation -> {

                }
                default -> throw new IllegalStateException("Unexpected value: " + object);
            }
        }
    }

    private void handleGoalZone(GoalZone goalZone) {
        goalZones.add(goalZone.point());
    }

    private void handleAttached(Attached attached) {
        this.attached.add(attached.point());
    }

    private void handleRoleZone(RoleZone roleZone) {
        roleZones.add(roleZone.point());
    }

    private void handleTeam(Team team) {
        this.team = team.value();
    }

    private void handleNorm(Norm norm) {
        norms.add(norm);
    }

    private void handleEnergy(Energy energy) {
        this.energy = energy.value();
    }

    private void handleScore(Score score) {
        this.score = score.value();
    }

    private void handleYourRole(YourRole yourRole) {
        currentRoleName = yourRole.name();
    }

    private void handleLastAction(LastAction lastAction) {
        this.lastAction.setName(lastAction.name());
    }

    private void handleName(Name name) {
        this.name = name.value();
    }

    private void handleSteps(Steps steps) {
        this.steps = steps.value();
    }

    private void handleLastActionResult(LastActionResult lar) {
        lastAction.setSuccessfulMessage(lar.value());
    }

    private void handleTeamSize(TeamSize teamSize) {
        this.teamSize = teamSize.size();
    }

    private void handleLastActionParameters(LastActionParameters lastActionParameters) {
        lastAction.setLastActionParameters(lastActionParameters.value());
    }

    private void handleStep(Step step) {
        currentStep = step.value();
    }

    private void handleRole(Role role) {
        possibleRoles.putIfAbsent(role.getName(), role);
    }

    private void handleDeactivated(Deactivated deactivated) {
        isDeactivated = deactivated.value();
    }

    private void handleThing(g6Agent.ourPercepts.Thing thing) {
        final Point position = new Point(thing.x(), thing.y());
        final Thing.Type type = thing.type();
        listOfAllObstacles.setListOfAllObstacles(type.name(), position);
        switch (type.name()) {
            case "obstacle" -> obstacles.add(position);
            case "entity" -> perceivedAgents.add(new AgentEntry(thing.details(), position));
            case "block" -> blocks.add(new Block(position, thing.type().name()));
            case "dispenser" -> dispensers.add(new Block(position, thing.type().name()));
            case "marker" -> markers.add(new Marker(position, thing.details()));
            default -> new IllegalArgumentException("unhandleed \"thing\"").printStackTrace();
        }

    }

    private void handleActionID(ActionID actionID) {
        currentId = actionID.id();
    }

    private void handleTask(Task task) {
        tasks.add(task);
    }

    private void handle(Deadline from) {
        new Throwable().printStackTrace();
    }

    private Object handle(YourRole yourRole) {
        new Throwable().printStackTrace();
        return null;
    }

    private Object handle(Role role) {
        new Throwable().printStackTrace();
        return null;
    }

    private void notifyListenersOfLastAction() {
        for (LastActionListener listener : lastActionListeners) {
            listener.reportLastAction(lastAction);
        }
    }


    private void handleRolePercept(Percept percept) throws Exception {
        if (!(percept.getParameters().size() == 6 || percept.getParameters().size() == 1)) {
            throw new Exception("PERCEPTION MODULE: role with unforeseen parameter size : " + percept + "size :" + percept.getParameters().size());
        }
        //case is the current Role of the Agent
        if (percept.getParameters().size() == 1) {
            this.currentRoleName = ((Identifier) percept.getParameters().get(0)).toProlog();
        } else {
            //Check if Role is already known if not insert to HashMap
            if (possibleRoles.get(((Identifier) percept.getParameters().get(0)).toProlog()) == null) {

                List<String> possibleActions = new ArrayList<>(((ParameterList) percept.getParameters().get(2)).size());
                for (Parameter p : (ParameterList) percept.getParameters().get(2)) {
                    possibleActions.add(((Identifier) p).toProlog());
                }
                List<Integer> movement = new ArrayList<>();
                for (Parameter p : (ParameterList) percept.getParameters().get(3)) {
                    movement.add(((Numeral) p).getValue().intValue());
                }
                Role role = new Role(((Identifier) percept.getParameters().get(0)).toProlog(), ((Numeral) percept.getParameters().get(1)).getValue().intValue(), possibleActions, movement, ((Numeral) percept.getParameters().get(4)).getValue().doubleValue(), ((Numeral) percept.getParameters().get(5)).getValue().intValue());
                this.possibleRoles.put(role.getName(), role);
            }
        }
    }

    private void handleNorm(Percept percept) throws Exception {
        if (percept.getParameters().size() != 5) {
            throw new Exception("PERCEPTION MODULE: norm with unforeseen parameter size");
        }
        this.norms.add(Norm.from(percept));
    }

    private void handleGoalZone(Percept percept) throws Exception {
        if (percept.getParameters().size() != 2) {
            throw new Exception("PERCEPTION MODULE: goalZone with unforeseen parameter size");
        }
        this.goalZones.add(new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue()));
    }

    private void handleRoleZone(Percept percept) throws Exception {
        if (percept.getParameters().size() != 2) {
            throw new Exception("PERCEPTION MODULE: RoleZone with unforeseen parameter size");
        }
        this.roleZones.add(new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue()));
    }

    private void handleTask(Percept percept) throws Exception {
        if (percept.getParameters().size() != 4) {
            throw new Exception("PERCEPTION MODULE: Task with unforeseen parameter size");
        }
        tasks.add(new Task(percept));
    }

    private void handleLastActionResult(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: lastActionResult with unforeseen parameter size");
        }
        lastAction.setSuccessfulMessage(
                ((Identifier) percept.getParameters().get(0)).toProlog()
        );
    }

    private void handleTeam(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: team with unforeseen parameter size");
        }
        this.team = ((Identifier) percept.getParameters().get(0)).toProlog();
    }

    private void handleDeactivated(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: deactivated with unforeseen parameter size");
        }
        isDeactivated = ((Identifier) percept.getParameters().get(0)).toProlog().equals("true");
    }

    private void handleLastActionParams(Percept percept) {
        lastAction.setLastActionParameters(percept.getParameters());
    }

    private void handleName(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: name with unforeseen parameter size");
        }
        this.name = ((Identifier) percept.getParameters().get(0)).toProlog();
    }

    private void handleEnergy(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: energy with unforeseen parameter size");
        }
        this.energy = ((Numeral) percept.getParameters().get(0)).getValue().intValue();

    }

    private void handleLastAction(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: lastAction with unforeseen parameter size");
        }
        Parameter p = percept.getParameters().get(0);
        lastAction.setName(((Identifier) p).toProlog());
    }

    private void handleActionIDPercept(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: actionID with unforeseen parameter size");
        }
        Parameter param = percept.getParameters().get(0);
        if (param instanceof Numeral) {
            currentId = ((Numeral) param).getValue().intValue();
        }
    }

    private void handle(ActionID actionId) {
        currentId = actionId.id();
    }

    private void handleThingPercept(Percept percept) throws Exception {

        String identifier = ((Identifier) percept.getParameters().get(2)).toProlog();
        if (percept.getParameters().size() != 4) {
            throw new Exception("PERCEPTION MODULE: obstacle with unforeseen parameter size");
        }
        if ("obstacle".equals(identifier)) {
            Point positionOfObstacle = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue());
            obstacles.add(positionOfObstacle);
            listOfAllObstacles.setListOfAllObstacles(identifier, positionOfObstacle);
        } else if ("entity".equals(identifier)) {
            Point positionOfAgent = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue());
            String teamName = ((Identifier) percept.getParameters().get(3)).toProlog();
            perceivedAgents.add(new AgentEntry(teamName, positionOfAgent));
            listOfAllObstacles.setListOfAllObstacles(identifier, positionOfAgent);

        } else if ("block".equals(identifier)) {
            Point pointerBlock = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue());

            this.blocks.add(new Block(pointerBlock, ((Identifier) percept.getParameters().get(3)).toProlog()));
            listOfAllObstacles.setListOfAllObstacles(identifier, pointerBlock);

        } else if ("dispenser".equals(identifier)) {

            Point pointerDispenser = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue());

            this.dispensers.add(new Block(pointerDispenser, ((Identifier) percept.getParameters().get(3)).toProlog()));

            listOfAllObstacles.setListOfAllObstacles(identifier, pointerDispenser);

        } else if ("marker".equals(identifier)) {
            Point pointerMarker = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue());

            this.markers.add(new Marker(pointerMarker, ((Identifier) percept.getParameters().get(3)).toProlog()));
            listOfAllObstacles.setListOfAllObstacles(identifier, pointerMarker);
        } else {
            System.out.println("UNHANDLED PERCEPT : " + percept);
        }

    }

    private void handleScorePercept(Percept percept) throws Exception {
        if (percept.getParameters().size() != 1) {
            throw new Exception("PERCEPTION MODULE: score with unforeseen parameter size");
        }
        Parameter param = percept.getParameters().get(0);
        if (param instanceof Numeral) {
            score = ((Numeral) param).getValue().intValue();
        }
    }

    private void handle(Score score) {
        this.score = score.value();
    }

    @Override
    public boolean isReadyForAction() {
        //TODO Checking for deactivation leads to failed server connections search for cause!
        //if (isDeactivated) { return false;}
        if (!isActionIdCheckedSuccessfully) {
            checkActionID();
        }
        return isActionIdCheckedSuccessfully;
    }


    private void checkActionID() {
        if (currentId > lastID) {
            lastID = currentId;
            this.isActionIdCheckedSuccessfully = true;
        }
    }

    private void clearShortTermMemory() {
        obstacles = new ArrayList<>();
        isActionIdCheckedSuccessfully = false;
        lastAction = new LastActionMemory();
        isDeactivated = false;
        perceivedAgents = new ArrayList<>();
        tasks = new ArrayList<>();
        dispensers = new ArrayList<>();
        blocks = new ArrayList<>();
        roleZones = new ArrayList<>();
        goalZones = new ArrayList<>();
        norms = new ArrayList<>();
        markers = new ArrayList<>();
        attached = new ArrayList<>();
    }

    @Override
    public List<Point> getFriendlyAgents() {
        List<Point> points = new ArrayList<>();
        for (AgentEntry agent : perceivedAgents) {
            if (agent.team.equals(this.team)) {
                points.add(agent.coordinate());
            }
        }
        return points;
    }

    @Override
    public List<Point> getEnemyAgents() {
        List<Point> points = new ArrayList<>();
        for (AgentEntry agent : perceivedAgents) {
            if (!agent.team.equals(this.team)) {
                points.add(agent.coordinate());
            }
        }
        return points;
    }

    @Override
    public List<Task> getTasks() {

        return tasks.stream().filter(t -> t.getEnd() >= currentStep).collect(Collectors.toList());
    }

    @Override
    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public List<Block> getDispensers() {
        return dispensers;
    }

    @Override
    public List<Marker> getMarkers() {
        return markers;
    }

    @Override
    public List<Point> getRoleZones() {
        return roleZones;
    }

    @Override
    public List<Point> getGoalZones() {
        return this.goalZones;
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public int getCurrentStep() {
        return currentStep;
    }

    @Override
    public List<Role> getPossibleRoles() {
        List<Role> roles = new ArrayList<>();
        possibleRoles.forEach((key, role) -> roles.add(role));
        return roles;
    }

    @Override
    public Role getCurrentRole() {
        return possibleRoles.get(currentRoleName);
    }

    @Override
    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public List<Point> getAttached() {
        return attached;
    }

    @Override
    public List<Block> getAttachedBlocks() {
        List<Block> blocksAttached = new ArrayList<>(attached.size());
        if (this.attached.isEmpty()) {
            return blocksAttached;
        }
        for (Point p : attached) {
            for (Block block : blocks) {
                if (block.getCoordinates().equals(p)) {
                    blocksAttached.add(block);
                }
            }
        }
        return blocksAttached;
    }

    @Override
    public List<Norm> getNorms() {
        return norms;
    }

    @Override
    public void addLastActionListener(LastActionListener listener) {
        this.lastActionListeners.add(listener);
    }

    @Override
    public List<Point> getObstacles() {
        return obstacles;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public String getTeam() {
        return this.team;
    }

    @Override
    public LastActionMemory getLastAction() {
        return lastAction;
    }


}
