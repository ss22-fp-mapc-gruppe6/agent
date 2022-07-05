package g6Agent.run;

import java.io.IOException;
import java.net.URISyntaxException;

public class Both {

    //run server + agents
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
