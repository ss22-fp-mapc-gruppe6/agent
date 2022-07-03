package g6Agent.perceptionAndMemory.Interfaces;

import g6Agent.perceptionAndMemory.Enties.LastActionMemory;

/**
 * Interface to listen to new LastActionResults in @link{PerceptionAndMemoryImplementation}
 */
public interface LastActionListener {
    /**
     * Notifies the Listener of the LastActionResult
     *
     * @param lastAction the LastActionResult
     */
    void reportLastAction(LastActionMemory lastAction);
}
