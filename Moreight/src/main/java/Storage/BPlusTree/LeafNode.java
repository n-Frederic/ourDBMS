package Storage.BPlusTree;

import Storage.Page.Page;
import Storage.Page.Row;

import java.io.IOException;
import java.util.*;

public class LeafNode extends BPlusNode{
    private Map<Key,Row> values;  // 存储键值对
    private Page page;               // 叶子节点对应的存储页


    public LeafNode(Page p, int maxKeys) {
        super(maxKeys);
        values = new TreeMap<>();  // 用 TreeMap 保证键值对有序
        page = p;         // 每个叶子节点对应一个 Page
    }

    // 插入数据到叶子节点
    public void insert(Row row) throws IOException {
        String primaryKey = page.getSchema().getPrimaryKeyName();
        page.insert(row);  // 将数据存储到该页
    }

    @Override
    public BPlusNode split() {
        Page newPage = new Page(page.getPageId(), page.getSchema());
        LeafNode newLeaf = new LeafNode(newPage,maxKeys);

        int mid = values.size()/2;
        List<Key> keys = new ArrayList<>(values.keySet());

        for (int i = mid; i < keys.size(); i++) {
            Key key = keys.get(i);
            Row row = values.get(key);
            try {
                newPage.insert(row);
            } catch (IOException e) {
                e.printStackTrace();
            }
            newLeaf.values.put(key, row);
        }

        // 删除原来的一半
        for (int i = mid; i < keys.size(); i++) {
            Key key = keys.get(i);
            values.remove(key);
         }

        InternalNode newRoot = new InternalNode(maxKeys);
        Object promotedKey = keys.get(mid);  // 中间 key 提升
        newRoot.addChild(promotedKey, this, newLeaf);

        return newRoot;
    }
}
