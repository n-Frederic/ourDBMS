package Storage.BPlusTree;

import Storage.Page.Tuple;

/**
 * B+树
 * @author zhangtianlong
 */
public class BpTree implements Tree {

    /**
     * B+树根节点
     */
    BpNode root;

    /**
     * B+树叶子节点的头结点
     */
    BpNode head;

    public BpTree() {
        root = new BpNode(true, true);
        head = root;
    }

    public BpNode getHead() {
        return head;
    }

    public BpNode getRoot() {
        return root;
    }

    @Override
    public Tuple find(Tuple key) {
        return root.get(key);
    }

    @Override
    public void insert(Tuple key) {
        System.out.println("insert " + key.getValues()[0]);
        root.insert(key, this);
    }

    @Override
    public boolean remove(Tuple key) {
        return root.remove(key, this);
    }

    /**
     * 验证树本身是否符合B+树规范
     */
    public boolean validate() {
        if (root.validate()) return true;
        else return false;
    }

    /**
     * 清空 B+ 树，释放所有节点
     */
    public void truncate() {
        if (root != null) {
            // 递归删除所有节点
            deleteNode(root);
        }
        // 重置树结构
        root = null;
        head = null;
    }

    /**
     * 递归删除节点及其子树
     * @param node 当前节点
     */
    private void deleteNode(BpNode node) {
        if (node == null) {
            return;
        }

        if (!node.isLeaf) {
            for (BpNode child : node.children) {
                deleteNode(child);
            }
        }

        node.entries.clear();
        node.children.clear();
        node.parent = null;
        node.previous = null;
        node.next = null;
    }
}
