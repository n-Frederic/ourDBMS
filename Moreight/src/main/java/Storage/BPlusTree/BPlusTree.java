package Storage.BPlusTree;

import Storage.Page.Page;
import Table.Schema;

public class BPlusTree {

    private InternalNode root;  // 根节点
    private int maxKeys;     // 每个节点的最大关键字数
    private int nextPageId = 0;  // 用于分配 Page 的唯一 id
    private Schema schema;   // 表结构，用于创建 Page

    public BPlusTree(int maxKeys, Schema schema) {
        this.maxKeys = maxKeys;
        this.schema = schema;

        // 初始化根节点，首先创建一个空的 Page
        Page rootPage = new Page(nextPageId++, schema);

        this.root = new InternalNode(maxKeys);  // 初始时根节点是叶子节点
    }

    // 打印树的结构（调试用）
    public void printTree() {
        root.printNode(0);
    }

    public LeafNode getRoot() {
        return root;
    }

    public void addNode(LeafNode root, LeafNode node) {
        InternalNode temp;
        while(!temp.children.isEmpty())
    }
}
