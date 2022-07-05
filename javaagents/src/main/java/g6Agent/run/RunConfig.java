package g6Agent.run;

import java.util.Map;

import static g6Agent.run.RunConfig.Configuration.AStarWithAttachedBlockRotation;
import static g6Agent.run.RunConfig.Configuration.Tournament3ClientOnly;

public class RunConfig {
    static boolean openBrowser = false;
    static Configuration selected = Tournament3ClientOnly;
    static Map<Configuration, String[]> configMap = Map.of(
            AStarWithAttachedBlockRotation, new String[]{
                    "/HardcodedAStarRotation",
                    "/HardcodedAStarRotation/config.json"
            },
            Tournament3ClientOnly, new String[]{
                    "/Turnier3"
            }
    );

    enum Configuration {
        AStarWithAttachedBlockRotation,
        Tournament3ClientOnly
    }
}
