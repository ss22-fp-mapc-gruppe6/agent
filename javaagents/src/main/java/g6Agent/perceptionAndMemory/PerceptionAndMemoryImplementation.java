package g6Agent.perceptionAndMemory;

import eis.iilang.*;
import g6Agent.perceptionAndMemory.Interfaces.AgentVisionReporter;
import g6Agent.perceptionAndMemory.Interfaces.LastActionListener;
import g6Agent.perceptionAndMemory.Enties.*;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemoryInput;
import g6Agent.services.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import g6Agent.environment.GridObject;

public class PerceptionAndMemoryImplementation implements PerceptionAndMemory, PerceptionAndMemoryInput {

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
    private List<Block> attachedBlocks;

    private final AttachedBlocksModule attachedBlocksController;

    private record AgentEntry(String team, Point coordinate) {}

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
        this.lastActionListeners = new ArrayList<>(2);

        this.attachedBlocksController = new AttachedBlocksModule(this);
        addLastActionListener(attachedBlocksController);
    }

    void setVisionReporter(AgentVisionReporter reporter){
        this.visionReporter = reporter;
    }
    @Override
    public boolean isDeactivated() {
        return isDeactivated;
    }

    @Override
    public void handlePercepts(List<Percept> perceptInput) {
        if (!perceptInput.isEmpty()) {
            clearShortTermMemory();
            try {
                for (Percept percept : perceptInput) {
                    if (percept.getName().equals("actionID")) {
                        handleActionIDPercept(percept);
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
                    } else if (percept.getName().equals("attached")){
                        this.attached.add(new Point(((Numeral)percept.getParameters().get(0)).getValue().intValue(),
                                ((Numeral)percept.getParameters().get(1)).getValue().intValue()));
                    }else if (percept.getName().equals("simEnd")){
                        lastID = -1;
                        currentId = -1;
                        System.out.println("NEW GAME");
                    }
                    //ignore cases
                    else if (!(percept.getName().equals("simStart")
                            || percept.getName().equals("requestAction")
                            || percept.getName().equals("deadline")
                            || percept.getName().equals("timestamp"))
                    ) {
                        System.out.println("UNHANDLED PERCEPT : " + percept);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (visionReporter != null) {
                visionReporter.handleStep();
            }
            notifyListenersOfLastAction();
            attachedBlocksController.checkClearConditions();
            if (visionReporter != null){
                visionReporter.reportMyVision(dispensers, blocks, roleZones, goalZones, obstacles);
                visionReporter.updateMyVisionWithSightingsOfOtherAgents();
            }
        }
    }

    private void notifyListenersOfLastAction() {
        for (LastActionListener listener : lastActionListeners){
            listener.reportLastAction(lastAction);
        }
    }


    private void handleRolePercept(Percept percept) throws Exception {
        if (!(percept.getParameters().size() == 6 || percept.getParameters().size() == 1)) {
            throw new Exception("PERCEPTION MODULE: role with unforeseen parameter size : " + percept + "size :" +percept.getParameters().size() );
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
                Role role = new Role(
                        ((Identifier) percept.getParameters().get(0)).toProlog(),
                        ((Numeral) percept.getParameters().get(1)).getValue().intValue(),
                        possibleActions,
                        movement,
                        ((Numeral) percept.getParameters().get(4)).getValue().doubleValue(),
                        ((Numeral) percept.getParameters().get(5)).getValue().intValue()
                );
                this.possibleRoles.put(role.getName(), role);
            }
        }
    }

    private void handleNorm(Percept percept) throws Exception {
        if (percept.getParameters().size() != 5) {
            throw new Exception("PERCEPTION MODULE: norm with unforeseen parameter size");
        }
        List<Function> listOfParameters = new ArrayList<>();
        for (Parameter p: (ParameterList) percept.getParameters().get(3)) {
            listOfParameters.add((Function) p);
        }

        this.norms.add(new Norm(
                ((Identifier) percept.getParameters().get(0)).toProlog(),
                ((Numeral) percept.getParameters().get(1)).getValue().intValue(),
                ((Numeral) percept.getParameters().get(2)).getValue().intValue(),
                listOfParameters,
                ((Numeral) percept.getParameters().get(4)).getValue().intValue()
                ));
    }
    @Override
    public void handleGoalZone(Percept percept) throws Exception {
        if (percept.getParameters().size() != 2) {
            throw new Exception("PERCEPTION MODULE: goalZone with unforeseen parameter size");
        }
        this.goalZones.add(new Point(
                ((Numeral)percept.getParameters().get(0)).getValue().intValue(),
                ((Numeral)percept.getParameters().get(1)).getValue().intValue()
        ));
    }
    @Override
    public void handleRoleZone(Percept percept) throws Exception {
        if (percept.getParameters().size() != 2) {
            throw new Exception("PERCEPTION MODULE: RoleZone with unforeseen parameter size");
        }
        this.roleZones.add(new Point(
                ((Numeral)percept.getParameters().get(0)).getValue().intValue(),
                ((Numeral)percept.getParameters().get(1)).getValue().intValue()
        ));
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
        this.team = ((Identifier)percept.getParameters().get(0)).toProlog();
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
        this.energy = ((Numeral)percept.getParameters().get(0)).getValue().intValue();

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
    @Override
    public void handleThingPercept(Percept percept) throws Exception {
        if (percept.getParameters().size() != 4) {
            throw new Exception("PERCEPTION MODULE: obstacle with unforeseen parameter size");
        }
        String identifier = ((Identifier)percept.getParameters().get(2)).toProlog();
        if ("obstacle".equals(identifier)) {
                Point positionOfObstacle = new Point(
                        ((Numeral) percept.getParameters().get(0)).getValue().intValue(),
                        ((Numeral) percept.getParameters().get(1)).getValue().intValue());
                obstacles.add(positionOfObstacle);
                listOfAllObstacles.setListOfAllObstacles(identifier, positionOfObstacle);
        } else if ("entity".equals(identifier)) {
            Point positionOfAgent = new Point(
                    ((Numeral) percept.getParameters().get(0)).getValue().intValue(),
                    ((Numeral) percept.getParameters().get(1)).getValue().intValue());
            String teamName = ((Identifier) percept.getParameters().get(3)).toProlog();
            perceivedAgents.add(new AgentEntry(teamName, positionOfAgent));
            listOfAllObstacles.setListOfAllObstacles(identifier, positionOfAgent);

        } else if ("block".equals(identifier)) {
            Point pointerBlock = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(),
                    ((Numeral) percept.getParameters().get(1)).getValue().intValue());

            this.blocks.add(new Block(pointerBlock, ((Identifier) percept.getParameters().get(3)).toProlog()));
            listOfAllObstacles.setListOfAllObstacles(identifier, pointerBlock);

        } else if ("dispenser".equals(identifier)) {

            Point pointerDispenser = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(),
                    ((Numeral) percept.getParameters().get(1)).getValue().intValue());

            this.dispensers.add(new Block(pointerDispenser,((Identifier) percept.getParameters().get(3)).toProlog()));

            listOfAllObstacles.setListOfAllObstacles(identifier, pointerDispenser);

        } else if("marker".equals(identifier)) {
            Point pointerMarker = new Point(((Numeral) percept.getParameters().get(0)).getValue().intValue(),
                    ((Numeral) percept.getParameters().get(1)).getValue().intValue());

            this.markers.add(new Marker(pointerMarker,((Identifier) percept.getParameters().get(3)).toProlog()));
            listOfAllObstacles.setListOfAllObstacles(identifier, pointerMarker);
        }else{
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

    @Override
    public boolean isReadyForAction() {
        //TODO Checking for deactivation leads to failed server connections search for cause!
        //if (isDeactivated) { return false;}
        if (!isActionIdCheckedSuccessfully){checkActionID();}
        return isActionIdCheckedSuccessfully;
    }


    private void checkActionID(){
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
        attachedBlocks = null;
    }

    @Override
    public List<Point> getFriendlyAgents() {
        List<Point> points = new ArrayList<>();
        for (AgentEntry agent : perceivedAgents) {
            if (agent.team.equals(this.team) && !agent.coordinate.equals(new Point(0,0))){
                points.add(agent.coordinate());
            }
        }
        return points;
    }

    @Override
    public List<Point> getEnemyAgents() {
        List<Point> points = new ArrayList<>();
        for (AgentEntry agent : perceivedAgents) {
            if (!agent.team.equals(this.team)){
                points.add(agent.coordinate());
            }
        }
        return points;
    }

    @Override
    public List<Task> getActiveTasks() {

        return tasks.stream().filter(t -> t.getEnd() >= currentStep).collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllTasks() {
        return tasks;
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
        possibleRoles.forEach( (key, role) -> roles.add(role));
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

    /*
    @Override
    public List<Point> getAttached() {
        return attached;
    }
    */


    @Override
    public List<Block> getAttachedBlocks() {
        if (this.attachedBlocks != null) return this.attachedBlocks;
        List<Block> blocksAttached = new ArrayList<>(attached.size());
        if (this.attached.isEmpty()){
            return blocksAttached;
        }
        for(Point p : attached){
            for(Block block : blocks){
                if(block.getCoordinates().equals(p)){
                    blocksAttached.add(block);
                }
            }
        }
        this.attachedBlocks = blocksAttached;
        return blocksAttached;
    }

    @Override
    public List<Block> getDirectlyAttachedBlocks() {
        //return getAttachedBlocks().stream().filter(block -> block.getCoordinates().isAdjacent()).collect(Collectors.toList());
        return attachedBlocksController.getAttachedBlocks();
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
    public List<AgentNameAndPosition> getKnownAgents() {
        if (this.visionReporter == null) return new ArrayList<>();
        return visionReporter.getKnownAgentPositions();
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
