package g6Agent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static g6Agent.Run.Configuration.*;

public class Run {
    static Configuration selected = A;
    enum Configuration {
        A, B
    }

    static Map<Configuration, String[]> configMap = Map.of(
            A, new String[]{
                    "/conf/agents/MyTestAgent",
                    "/conf/server/SampleConfig.json"
            },
            B, new String[]{
                    "/conf/agents/MySingleAgent",
                    "/conf/server/SampleConfig.json"
            }
    );
    static String baseDirectory = "javaagents/src/main/resources";


    public static class Agents {
        public static void main(String[] args) {
            Main.main(new String[]{Run.baseDirectory + configMap.get(selected)[0]});
        }
    }

    public static class Server {
        public static void main(String[] args) throws URISyntaxException, IOException {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:8000"));
            }

            massim.Server.main(new String[]{"-conf", baseDirectory + configMap.get(selected)[1], "--monitor"});
        }
    }

    //run both
    public static void main(String[] args) {
        final Thread server = new Thread(() -> {
            try {
                Server.main(new String[]{});
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        server.setName("server");
        server.start();
        final Thread agents = new Thread(() -> Agents.main(new String[]{}));
        agents.setName("agents");
        agents.start();
    }

}
