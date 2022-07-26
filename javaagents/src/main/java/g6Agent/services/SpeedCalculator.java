package g6Agent.services;

import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import org.jetbrains.annotations.NotNull;

/**
 * Class to Calculate the Speed of an Agent
 *
 * @author Kai Müller
 */
public class SpeedCalculator {
    public static int determineSpeedOfLastAction(@NotNull LastActionMemory lastAction, @NotNull PerceptionAndMemory perceptionAndMemory) {

        //can move full movement
        if (lastAction.getSuccessMessage().equals("success")) {
            return calculateSpeed(perceptionAndMemory);
        }
        //moves less than full movement, more than 0 (for a speed of 2 always 1)
        if (lastAction.getSuccessMessage().equals("partial_success")) {
            return 1;
            //TODO Explorer for whom it could be 1 or 2 is unhandeld
        }
        //movement failed
        return 0;
    }

    public static int calculateSpeed(PerceptionAndMemory perceptionAndMemory) {
        if (perceptionAndMemory.getCurrentRole() == null) return 1; //failsave
        int speed;
        //logic to get array entry of speed
        if (perceptionAndMemory.getCurrentRole().getMovementSpeed().size() <= perceptionAndMemory.getDirectlyAttachedBlocks().size()) {
            speed = 0;
        } else {
            speed = perceptionAndMemory.getCurrentRole().getMovementSpeed().get(perceptionAndMemory.getDirectlyAttachedBlocks().size());
        }
        return speed;
    }
}
