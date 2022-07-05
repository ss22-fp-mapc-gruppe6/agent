package g6Agent.run;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Server {
    public static void main(String[] args) throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://localhost:8000"));
        }

        massim.Server.main(new String[]{"-conf", "javaagents/src/main/resources" + RunConfig.configMap.get(RunConfig.selected)[1], "--monitor"});
    }
}
