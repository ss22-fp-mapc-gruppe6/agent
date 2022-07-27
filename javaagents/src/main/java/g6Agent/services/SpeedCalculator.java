package g6Agent.services;

import g6Agent.perceptionAndMemory.Enties.Block;
import g6Agent.perceptionAndMemory.Enties.LastActionMemory;
import g6Agent.perceptionAndMemory.Enties.Role;
import g6Agent.perceptionAndMemory.Interfaces.PerceptionAndMemory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Class to Calculate the Speed of an Agent
 *
 * @author Kai Müller
 */
public class SpeedCalculator {
    /**
     * Calculates the speed of an lastAction Memory from the success message,
     * and the role in relation to the attached blocks.
     * @param lastAction                the lastActionMemory
     * @param directlyAttachedBlocks    the blocks
     * @param roleLastStep              the role the agent had last step
     * @return                          the speed
     */
    public static int determineSpeedOfLastAction(@NotNull LastActionMemory lastAction,
                                                 @NotNull List<Block> directlyAttachedBlocks,
                                                 @NotNull Role roleLastStep) {
        //if is no move action it has no speed
        if(!lastAction.getName().equals("move")) return 0;

        //can move full movement
        if (lastAction.getSuccessMessage().equals("success")) {
            return calculateSpeed(directlyAttachedBlocks, roleLastStep);
        }
        //moves less than full movement, more than 0 (for a speed of 2 always 1)
        if (lastAction.getSuccessMessage().equals("partial_success")) {
            return 1;
            //TODO Explorer for whom it could be 1 or 2 is unhandeld
        }
        //movement failed
        return 0;
    }

    /**
     * Calculates the speed of an role in relation to the attached Blocks
     * @param directlyAttachedBlocks    the attached blocks
     * @param role                      the role
     * @return                          the speed
     */
    public static int calculateSpeed(@NotNull List<Block> directlyAttachedBlocks, @NotNull Role role) {
        int speed;
        //logic to get array entry of speed
        if (role.getMovementSpeed().size() <= directlyAttachedBlocks.size()) {
            speed = 0;
        } else {
            speed = role.getMovementSpeed().get(directlyAttachedBlocks.size());
        }
        return speed;
    }
}
