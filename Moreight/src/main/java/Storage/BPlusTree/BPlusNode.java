package Storage.BPlusTree;


import java.util.List;
import java.util.ArrayList;


public abstract class BPlusNode {
    protected int maxKeys;
    protected List<Key> keys;  // 存储关键字


    public BPlusNode(int maxKeys) {
        this.maxKeys = maxKeys;
        this.keys = new ArrayList<>(maxKeys);

    }


    // 判断节点是否溢出
    public boolean isOverflow() {
        return keys.size() > maxKeys;
    }

    // 节点分裂操作
    public abstract BPlusNode split();

    // 打印节点（调试）
    public void printNode(int level) {
        String indent = "  ".repeat(level);
        System.out.println(indent + "Keys: " + keys);
        if (!children.isEmpty()) {
            for (BPlusNode child : children) {
                child.printNode(level + 1);
            }
        }
    }
}