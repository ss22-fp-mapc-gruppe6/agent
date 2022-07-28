package g6Agent.run;

import g6Agent.Main;

public class MovementTest {



        static String agents = "/MovementTest";
        static String server = "/MovementTest/alone.json";
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
            new Thread(() -> g6Agent.run.MovementTest.Server.main(new String[]{})).start();
            new Thread(() -> g6Agent.run.MovementTest.Agents.main(new String[]{})).start();
        }
}
