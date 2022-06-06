package g6Agent.embeddedServer;

import eis.iilang.IILElement;
import massim.ReplayWriter;
import massim.monitor.Monitor;
import massim.util.IOUtil;
import org.json.JSONObject;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SimulationTestUtil {

    ReplayWriter replayWriter;
    JSONObject config;
    String replayPath;
    Monitor monitor;

    public SimulationTestUtil() {
        System.out.println("SimulationTestUtil.SimulationTestUtil");
        IILElement.toProlog = true;
        try {
            config = IOUtil.readJSONObjectWithImport("../server/conf/SampleConfig.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        replayPath = "./target/" + config.getJSONObject("server").getString("replayPath");
        replayWriter = null;
        if (replayPath != null) {
            replayWriter = new ReplayWriter(replayPath);
        }

        try {
            monitor = new Monitor(8000);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
