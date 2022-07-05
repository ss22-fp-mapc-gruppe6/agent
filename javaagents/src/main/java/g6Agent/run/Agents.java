package g6Agent.run;

import g6Agent.Main;

public class Agents {
    public static void main(String[] args) {
        Main.main(new String[]{"javaagents/src/main/resources" + RunConfig.configMap.get(RunConfig.selected)[0]});
    }
}
