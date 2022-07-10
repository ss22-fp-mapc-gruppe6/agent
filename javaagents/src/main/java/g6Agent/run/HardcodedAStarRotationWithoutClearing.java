package g6Agent.run;

import g6Agent.Main;


public class HardcodedAStarRotationWithoutClearing {

    static String agents = "/HardcodedAStarRotationWithoutClearing";
    static String server = "/HardcodedAStarRotationWithoutClearing/config.json";
    static String baseDirectory = "javaagents/src/main/resources";


    public static class Agents {
        public static void main(String[] args) {
            Main.main(new String[]{HardcodedAStarRotationWithoutClearing.baseDirectory + agents});
        }
    }

    public static class Server {
        public static void main(String[] args) {
            massim.Server.main(new String[]{"-conf", baseDirectory + server, "--monitor"});
        }
    }

    //run both
    public static void main(String[] args) {
        new Thread(() -> Server.main(new String[]{})).start();
        new Thread(() -> Agents.main(new String[]{})).start();
    }

}
