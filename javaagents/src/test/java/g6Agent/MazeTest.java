package g6Agent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MazeTest {

    private String rotationMaze = """
                sb  xxxxxxxxxxxxxxxxxx                
                              xxxxxxxx                
                    xxxxxxxxx xxxxxxxx                
                   xx       x xxxxxxxx                
                   xx xxxxx x       xx                
                   xx xxtxx xxxxxxx xx                
                   xx xx xx         xx                
                   xx xx xxxxxxxxxxxxx                
                   xx    xxxxxxxxxxxxx       
                   xxxxxxxxxxxxxxxxxxx                
                """;
    private String bigMaze = """

                xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                xx                                 
                x   xxxxxxxxxxxxxxxxxxxxxxxxxxxx xx
                x xxxx                      xxxx xx
                x xxxx xxxxxxxxxxxxxxxxxxxx xxxx xx
                x xxxx xx                xx xxxx xx
                x xxxx xx xxxxxxxxxxxxxx xx xxxx xx
                x xxxx xx x x          x xx xxxx xx
                x xxxx xx x x xxxxxxxx x xx xxxx xx
                x xxxx xx x x xx    xx x xx xxxx xx
                x xxxx xx   x xx xx xx x xx xxxx xx
                x xxxx xx xxx xx xxtxx x xx xxxx xx
                x xxxx xx xxx xx xxxxx x xx xxxx xx
                x xxxx xx xxx xx       x xx xxxx xx
                x xxxx xx xxx xxxxxxxxxx xx xxxx xx
                x   xx xx xxx             x xxxx xx
                xxx xx xx xxxxxxxxxxxxxxxxx xxxxxxx
                xxx xx      xxxxxxxxxxxxxxx xxxxxxx
                xxx  xxxxxxxxxxxxxxxxxxxxxx xxxxxxx
                xxx                         xxxxxxx
                xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

                                """;
    private String bigMaze2 = """

                xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                xx                            
                x   xxxxxxxxxxxxxxxxxxxxxxxxxx
                x xxxx                      xx
                x xxxx xxxxxxxxxxxxxxxxxxxx xx
                x xxxx xx                xx xx
                x xxxx xx xxxxxxxxxxxxxx xx xx
                x xxxx xx x x          x xx xx
                x xxxx xx x x xxxxxxxx x xx xx
                x xxxx xx x x xx    xx x xx xx
                x xxxx xx   x xx xx xx x xx xx
                x xxxx xx xxx xx xxtxx x xx xx
                x xxxx xx xxx xx xxxxx x xx xx
                x xxxx xx xxx xx       x xx xx
                x xxxx xx xxx xxxxxxxxxx xx xx
                x   xx xx xxx             x xx
                xxx xx xx xxxxxxxxxxxxxxxxx xx
                xxx xx      xxxxxxxxxxxxxxx xx
                xxx  xxxxxxxxxxxxxxxxxxxxxx xx
                xxx                         xx
                xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

                                """;
    private String smallMaze = """
                xxxxxxxxx 
                x       x x   
                x xxxxx x x   
                x x   x x x   
                x x x x x x   
                x x xtx x x   
                x x xxx x x   
                x x     x x   
                x xxxxxxx x   
                x         x   
                xxxxxxxxxxx
                """;

    @Test
    public void name() {
        final String[] lines = rotationMaze.lines().sequential().toArray(String[]::new);
        List<Tuple<Integer, Integer>> blocks = new ArrayList<>();
        for (int y = 0; y < lines.length; y++) {
            final char[] chars = lines[y].toCharArray();
            for (int x = 0; x < chars.length; x++) {
                if (chars[x] == 's') System.out.println("move " + x + " " + y + " agentA1");
                if (chars[x] == 't') System.out.println("add " + x + " " + y + " dispenser d1");
                if (chars[x] == 'b') System.out.println("add " + x + " " + y + " block b1");
                if (chars[x] == 'x') System.out.println("terrain " + x + " " + y + " obstacle");
            }
        }
    }
}
