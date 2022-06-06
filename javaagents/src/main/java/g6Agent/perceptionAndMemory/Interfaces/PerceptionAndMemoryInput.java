package g6Agent.perceptionAndMemory.Interfaces;

import eis.iilang.Percept;

/**
 * Interface, that allows Input to Perception and Memory
 */
public interface PerceptionAndMemoryInput {
    void handleGoalZone(Percept percept) throws Exception;

    void handleRoleZone(Percept percept) throws Exception;

    void handleThingPercept(Percept percept) throws Exception;
}
