package g6Agent.ourPercepts;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Percept;

public record YourRole(String name) {
    public static YourRole from(Percept percept) {
        return new YourRole(((Identifier) percept.getParameters().get(0)).getValue());
    }
}
