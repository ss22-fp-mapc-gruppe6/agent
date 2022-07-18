package g6Agent.decisionModule.manhattanDistanceMove;

import g6Agent.actions.Clear;
import g6Agent.actions.G6Action;
import g6Agent.actions.Move;
import g6Agent.actions.Rotate;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import g6Agent.services.Direction;
import g6Agent.services.Point;
import g6Agent.services.Rotation;

public class ManhattanDistanceMove {
    public static G6Action nextAction(Point target, PerceptionAndMemory perceptionAndMemory){



        Direction direction = determineClosestdirection(target, perceptionAndMemory);


        for (Block attachedBlock : perceptionAndMemory.getDirectlyAttachedBlocks()) {
            if (!attachedBlock.getCoordinates().invert().equals(direction.getNextCoordinate())) {
                for (Point obstacle : perceptionAndMemory.getObstacles()) {
                    if (obstacle.equals(direction.rotate(Rotation.CLOCKWISE).getNextCoordinate())
                            || obstacle.equals(direction.getNextCoordinate().invert())
                            || obstacle.equals(direction.rotate(Rotation.COUNTERCLOCKWISE).getNextCoordinate())) {
                        return new Clear(obstacle);
                    }
                }
                return new Rotate(Rotation.CLOCKWISE);
            }
        }
        for (Point obstacle : perceptionAndMemory.getObstacles()) {
            if (direction.getNextCoordinate().equals(obstacle)) {
                return new Clear(obstacle);
            }
        }
        return new Move(direction);
    }

    private static Direction determineClosestdirection(Point target, PerceptionAndMemory perceptionAndMemory) {
        Direction direction = Direction.WEST;
        for (Direction possibleDirection : Direction.allDirections()){
            if(possibleDirection.getNextCoordinate().manhattanDistanceTo(new Point(0,0))
                    < direction.getNextCoordinate().manhattanDistanceTo(new Point(0,0))){
                direction = possibleDirection;
            }
        }
        return direction;
    }

}
