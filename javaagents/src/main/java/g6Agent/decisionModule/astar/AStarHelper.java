package g6Agent.decisionModule.astar;


import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static g6Agent.decisionModule.astar.AStar.*;

public class AStarHelper {
   static void visualize(Set<Wrapper> queue, Wrapper origin){
        List<Wrapper> l = new ArrayList<>(queue);
        final var map = new HashMap<Wrapper, TreeNode<Wrapper>>();
        for (Wrapper wrapper : l) {
            map.put(wrapper, new TreeNode<>(wrapper, new ArrayList<>()));
        }
       for (Wrapper wrapper : l) {
           while (wrapper.predecessor != null){
               wrapper = wrapper.predecessor;
               map.putIfAbsent(wrapper, new TreeNode<>(wrapper, new ArrayList<>()));
           }
       }
        for (Wrapper wrapper : l) {
            final var node = map.get(wrapper);
            final var predecessor = wrapper.predecessor;
            if (predecessor!=null) {
                final var node_predecessor = map.get(predecessor);
                node_predecessor.children.add(node);
            }
        }

       final var wrapperTreeNode = map.get(origin);
       PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);
       printStream.println("map.get(peek).toString() = \n" +
                   "" + wrapperTreeNode.toString());

   }
}
