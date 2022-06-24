package g6Agent.decisionModule;

import g6Agent.actions.G6Action;
import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.services.Point;

import java.util.List;

public interface IAStar {
    List<? extends G6Action> findShortestPath(Point target, List<Point> obstacles, int stepSize, List<Block> directlyAttachedBlocks);
}
