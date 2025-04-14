package Storage.BPlusTree;

import Storage.Page.Row;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class LeafNode {
    private List<Row> rows;         // 叶子节点存储的所有数据行
    private int nextLeafPageId;     // 指向下一个叶子节点的页 ID，便于范围查询

    public LeafNode() {
        rows = new ArrayList<>();
        nextLeafPageId = -1;  // 初始时没有下一个叶子节点
    }

    public void addRow(Row row) {
        rows.add(row);
        Collections.sort(rows);  // 插入后按键排序
    }

    public void removeRow(Row row) {
        rows.remove(row);
    }

    public List<Row> getRows() {
        return rows;
    }

    public int getNextLeafPageId() {
        return nextLeafPageId;
    }

    public void setNextLeafPageId(int nextLeafPageId) {
        this.nextLeafPageId = nextLeafPageId;
    }

    // 将叶子节点转化为字节数据，方便存储到磁盘
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rows);
        oos.writeInt(nextLeafPageId);
        return baos.toByteArray();
    }

    // 从字节数据恢复叶子节点
    public static LeafNode fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        List<Row> rows = (List<Row>) ois.readObject();
        int nextLeafPageId = ois.readInt();
        LeafNode leafNode = new LeafNode();
        leafNode.rows = rows;
        leafNode.nextLeafPageId = nextLeafPageId;
        return leafNode;
    }
}

