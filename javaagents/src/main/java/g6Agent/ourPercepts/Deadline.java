package g6Agent.ourPercepts;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;

public record Deadline(int value)
{
    public static Deadline from(Percept percept){
        return new Deadline(((Numeral) percept.getParameters().get(0)).getValue().intValue());
    }
}
