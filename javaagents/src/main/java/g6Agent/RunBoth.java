package g6Agent;

public class RunBoth {
    public static void main(String[] args) {
        final String base = "javaagents/src/main/resources";
        final Thread agents = new Thread(() -> {
            final String config = "/conf/agents/DummyAStarAgent";
            Main.main(new String[]{base + config});
        });
        agents.setName("agents");
        final Thread server = new Thread(() -> {
            final String config = "/conf/server/DummyAStarConfig.json";
            massim.Server.main(new String[]{"-conf", base + config, "--monitor"});
        });
        server.setName("server");
        server.start();
        agents.start();
    }
}
