package g6Agent.run;

import g6Agent.Main;


public class HardcodedAStarRotation2 {

    static String agents = "/HardcodedAStarRotation2";
    static String server = "/HardcodedAStarRotation2/config.json";
    static String baseDirectory = "javaagents/src/main/resources";


    public static class Agents {
        public static void main(String[] args) {
            Main.main(new String[]{HardcodedAStarRotation2.baseDirectory + agents});
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
