package g6Agent.run;

import g6Agent.Main;


public class Tunier4Online {

    static String agents = "/Tunier4Online";
    static String baseDirectory = "javaagents/src/main/resources";


    public static class Agents {
        public static void main(String[] args) {
            Main.main(new String[]{Tunier4Online.baseDirectory + agents});
        }
    }

    //run both
    public static void main(String[] args) {
        new Thread(() -> Agents.main(new String[]{})).start();
    }

}
