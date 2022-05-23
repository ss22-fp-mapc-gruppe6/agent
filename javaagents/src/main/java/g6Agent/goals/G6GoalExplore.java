package g6Agent.goals;

import g6Agent.actions.Clear;
import g6Agent.actions.G6Action;
import g6Agent.actions.Move;
import g6Agent.actions.Rotate;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;

public class G6GoalExplore implements Goal {
    private final PerceptionAndMemory perceptionAndMemory;
    private Direction fibonacciWalkDirection = Direction.NORTH;
    private int fibonnaciWalkCurrent = 1;
    private int fibbonacciWalkFormer = 1;
    private int fibbbonacciwalkCounter = 0;

    public G6GoalExplore(PerceptionAndMemory perceptionAndMemory) {
        this.perceptionAndMemory = perceptionAndMemory;
    }

    @Override
    public G6Action getNextAction() {
        return fibbonacciWalk();
    }

    private G6Action fibbonacciWalk() {
        if (fibbbonacciwalkCounter == fibonnaciWalkCurrent){
            int temp = fibonnaciWalkCurrent;
            fibonnaciWalkCurrent = fibonnaciWalkCurrent + fibbonacciWalkFormer;
            fibbonacciWalkFormer = temp;
            fibonacciWalkDirection = fibonacciWalkDirection.rotate(Rotation.CLOCKWISE);
            fibbbonacciwalkCounter = 0;
        }
        G6Action action = moveTo(fibonacciWalkDirection);
        if(action instanceof Move){
            fibbbonacciwalkCounter++;
        }
        return action;
    }
    private G6Action moveTo(Direction direction) {
        for(Block attachedBlock : perceptionAndMemory.getAttachedBlocks()){
            if(!attachedBlock.getCoordinates().invert().equals(direction.getNextCoordinate())){
                for (Point obstacle : perceptionAndMemory.getObstacles()){
                    if(obstacle.equals(direction.rotate(Rotation.CLOCKWISE).getNextCoordinate()) ||obstacle.equals(direction.getNextCoordinate().invert())){
                        return new Clear(obstacle);
                    }
                }
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        for(Point obstacle : perceptionAndMemory.getObstacles()){
            if(direction.getNextCoordinate().equals(obstacle)){
                return new Clear(obstacle);
            }
        }
        return new Move(direction);
    }

    @Override
    public boolean isSucceding() {
        return true;
    }

    @Override
    public boolean isFullfilled() {
        return false;
    }

    @Override
    public String getName() {
        return "G6GoalExplore";
    }
}
