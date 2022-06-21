package massim;

public class RUN {
    public static void main(String[] args) {
        final String base = "runner/src/main/resources";
        final Thread agents = new Thread(() -> {
            final String config = "/conf/agent/MyTestAgent";
            g6Agent.Main.main(new String[]{base + config});
        });
        agents.setName("agents");
        final Thread server = new Thread(() -> {
            final String config = "/conf/server/SampleConfig.json";
            massim.Server.main(new String[]{"-conf", base + config, "--monitor"});
        });
        server.setName("server");
        server.start();
        agents.start();
    }
}
