package massim.protocol.messages.scenario;

import java.util.List;

public abstract class Actions {

    public final static String NO_ACTION = "no_action";
    public final static String UNKNOWN_ACTION = "unknown_action";

    public final static String MOVE = "move";
    public final static String ATTACH = "attach";
    public final static String DETACH = "detach";
    public final static String ROTATE = "rotate";
    public final static String CONNECT = "connect";
    public final static String REQUEST = "request";
    public final static String SUBMIT = "submit";

    public static final List<String> ALL_ACTIONS = List.of(
            MOVE, ATTACH, DETACH, ROTATE, CONNECT, REQUEST, SUBMIT);

    public final static String RESULT_UNPROCESSED = "unprocessed";
    public final static String RESULT_PENDING = "pending";
    public final static String RESULT_SUCCESS = "success";
    public final static String RESULT_F = "failed";
    public final static String RESULT_F_RANDOM = "failed_random";
    public final static String RESULT_F_PARAMETER = "failed_parameter";
    public final static String RESULT_F_PATH = "failed_path";
    public final static String RESULT_F_PARTNER = "failed_partner";
}
