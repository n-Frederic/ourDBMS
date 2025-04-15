package Storage.BPlusTree;

import java.util.List;
import java.util.ArrayList;

public class InternalNode extends BPlusNode {
    public InternalNode(int maxKeys) {
        super(maxKeys);
    }


    @Override
    public BPlusNode split() {
        // 需要分裂内部节点
        int midIndex = keys.size() / 2;
        List<Integer> rightKeys = keys.subList(midIndex, keys.size());
        List<BPlusNode> rightChildren = children.subList(midIndex + 1, children.size());

        InternalNode newInternalNode = new InternalNode(maxKeys);
        newInternalNode.keys = rightKeys;
        newInternalNode.children = rightChildren;

        // 保留左侧的 keys 和 children
        keys = keys.subList(0, midIndex);
        children = children.subList(0, midIndex + 1);

        // 返回新内部节点
        return newInternalNode;
    }

    private BPlusNode getChildForInsertion(int key) {
        for (int i = 0; i < keys.size(); i++) {
            if (key < keys.get(i)) {
                return children.get(i);
            }
        }
        return children.get(keys.size());  // 默认返回最后一个子节点
    }
}