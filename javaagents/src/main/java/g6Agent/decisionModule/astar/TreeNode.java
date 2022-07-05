package g6Agent.decisionModule.astar;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class TreeNode<T> {

    final T item;
    final List<TreeNode<T>> children;

    public TreeNode(T item, List<TreeNode<T>> children) {
        this.item = item;
        this.children = children;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(item.toString());
        buffer.append('\n');
        for (Iterator<TreeNode<T>> it = children.iterator(); it.hasNext();) {
            TreeNode<T> next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }

}