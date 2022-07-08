package g6Agent;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TestMisc {
    @Test
    public void miscTests(){
        List<Integer> numbers = List.of(5,3,7,8,2,6,1);
        numbers = numbers.stream().sorted().collect(Collectors.toList());
        assert (numbers.get(0) == 1);
    }
}
