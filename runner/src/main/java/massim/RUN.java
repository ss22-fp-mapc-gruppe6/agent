package massim;

public class RUN {
    public static void main(String[] args) {
        final String conf = "runner/src/main/resources/conf";
        final Thread agents = new Thread(() -> {
            g6Agent.Main.main(new String[]{conf + "/agent/MyTestAgent"});
        });
        agents.setName("agents");
        final Thread server = new Thread(() -> {
            massim.Server.main(new String[]{"-conf", conf + "/server/SampleConfig.json", "--monitor"});
        });
        server.setName("server");
        server.start();
        agents.start();
    }
}
