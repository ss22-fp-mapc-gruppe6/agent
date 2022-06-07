package g6Agent.ourPercepts;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.*;
import g6Agent.perceptionAndMemory.Enties.Norm;
import g6Agent.perceptionAndMemory.Enties.Role;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static java.lang.Boolean.parseBoolean;

public class OurPerceptUtil {
    public static Object translate(Percept percept) {
        final List<Parameter> p = percept.getParameters();
        return switch (percept.getName()) {
            case "thing" -> withReflection(p, Thing.class);
            case "actionID" -> withReflection(p, ActionID.class);
            case "score" -> withReflection(p, Score.class);
            case "lastAction" -> withReflection(p, LastAction.class);
            case "energy" -> withReflection(p, Energy.class);
            case "name" -> withReflection(p, Name.class);
            case "lastActionParams" -> new LastActionParameters(p);
            case "deactivated" -> withReflection(p, Deactivated.class);
            case "team" -> withReflection(p, Team.class);
            case "lastActionResult" -> withReflection(p, LastActionResult.class);
            case "task" -> new g6Agent.perceptionAndMemory.Enties.Task(percept);
            case "goalZone" -> withReflection(p, GoalZone.class);
            case "ranking" -> withReflection(p, Ranking.class);
            case "roleZone" -> withReflection(p, RoleZone.class);
            case "norm" -> Norm.from(percept);
            case "step" -> withReflection(p, Step.class);
            case "steps" -> withReflection(p, Steps.class);
            case "teamSize" -> withReflection(p, TeamSize.class);
            case "attached" -> withReflection(p, Attached.class);
            case "simEnd" -> withReflection(p, SimEnd.class);
            case "simStart" -> withReflection(p, SimStart.class);
            case "requestAction" -> new RequestAction();
            case "deadline" -> withReflection(p, Deadline.class);
            case "timestamp" -> withReflection(p, Timestamp.class);
            case "violation" -> withReflection(p, Violation.class);
            case "role" -> p.size() == 6 ? Role.from(percept) : withReflection(p, YourRole.class);
            default -> throw new IllegalStateException("Unexpected value: " + percept.getName());
        };
    }

    public static <T> T withReflection(List<Parameter> parameters, Class<T> clazz) {
        try {
            final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length < 1) throw new IllegalArgumentException("no constructor for " + clazz);
            final Constructor<?> constructor = constructors[0];
            final java.lang.reflect.Parameter[] constructorParameters = constructor.getParameters();
            if (constructorParameters.length != parameters.size())
                throw new IllegalArgumentException("records first Constructor has to take as many arguments as you supply paramters");
            final Translator translator = Translator.getInstance();
            final Object[] os = new Object[parameters.size()];
            for (int i = 0; i < parameters.size(); i++) {
                final Parameter parameter = parameters.get(i);
                final java.lang.reflect.Parameter constructorParameter = constructorParameters[i];
                if (constructorParameter.getType().equals(Boolean.class) && parameter.getClass().equals(Identifier.class)) {
                    os[i] = parseBoolean(((Identifier) parameter).getValue());
                } else {
                    os[i] = translator.translate2Java(parameter, constructorParameter.getType());
                }

            }
            return (T) constructor.newInstance(os);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | TranslationException e) {
            throw new RuntimeException(e);
        }
    }

    public  static int num(Parameter p) {
        return ((Numeral) p).getValue().intValue();
    }

    public static String id(Parameter p) {
        return ((Identifier) p).getValue();
    }

}
