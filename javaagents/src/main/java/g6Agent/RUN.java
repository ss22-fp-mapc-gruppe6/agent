package g6Agent;

import massim.Server;

public class RUN {
    public static void main(String[] args) {
        final var agents = new Thread(() -> {
            Main.main(new String[]{"javaagents/conf/MyTestAgent"});
        });
        agents.setName("agents");
        final Thread server = new Thread(() -> {
            Server.main(new String[]{"-conf", "server/conf/SampleConfig.json", "--monitor"});
        });
        server.setName("server");
        server.start();
        agents.start();
    }
}
