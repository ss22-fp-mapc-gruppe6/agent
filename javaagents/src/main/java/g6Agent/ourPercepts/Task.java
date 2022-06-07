package g6Agent.ourPercepts;

import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;

import java.util.List;

import static g6Agent.ourPercepts.OurPerceptUtil.id;
import static g6Agent.ourPercepts.OurPerceptUtil.num;

public record Task(String name, int end, int reward, List<Parameter> requirements) {
    public Task(String name, int end, int reward, List<Parameter> requirements) {
        this.name = name;
        this.end = end;
        this.reward = reward;
        this.requirements = requirements;
    }

    public Task(String name, int end, int reward) {
        this(name, end, reward, null);
    }

//    public static Task from(Percept percept) {
//        final List<Parameter> p = percept.getParameters();
//        return new Task(id(p.get(0)), num(p.get(1)), num(p.get(2)), ((ParameterList) p.get(4)));
//    }

}
