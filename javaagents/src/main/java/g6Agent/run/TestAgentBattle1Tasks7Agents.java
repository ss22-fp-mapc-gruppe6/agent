package g6Agent.run;

import g6Agent.Main;

public class TestAgentBattle1Tasks7Agents {

    static String agents = "/TestAgentBattle1Tasks7Agents";
    static String server = "/TestAgentBattle1Tasks7Agents/server_conf.json";
    static String baseDirectory = "javaagents/src/main/resources";


    public static class Agents {
        public static void main(String[] args) {
            Main.main(new String[]{g6Agent.run.HardcodedAStarRotation.baseDirectory + agents});
        }
    }

    public static class Server {
        public static void main(String[] args) {
            massim.Server.main(new String[]{"-conf", baseDirectory + server, "--monitor"});
        }
    }

    //run both
    public static void main(String[] args) {
        new Thread(() -> g6Agent.run.TestAgentBattle1Tasks7Agents.Server.main(new String[]{})).start();
        new Thread(() -> g6Agent.run.TestAgentBattle1Tasks7Agents.Agents.main(new String[]{})).start();
    }
}
