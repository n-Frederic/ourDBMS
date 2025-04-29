package Storage.Page;

import Storage.BPlusTree.Tuple;
import Table.Schema;

import java.io.*;
import java.util.*;

public class Page {
    public static final int PAGE_SIZE = 16 * 1024; // 16KB

    private int pageId;    // page 的 id
    private Schema schema;
    private List<Tuple> tuples;

    public Page(int pageId, Schema schema) {
        this.pageId = pageId;
        this.schema = schema;
        this.tuples = new ArrayList<>();
    }

    /**
     * 将某一行插入page
     * @param tuple 待插入的行
     * @return 插入是否成功
     * @throws IOException
     */
    public boolean insert(Tuple tuple) throws IOException {
        if (isFull(tuple)) return false;
        tuples.add(tuple);
        return true;
    }

    /**
     * 得到行数组
     * @return 行数组
     */
    public List<Tuple> getTuples() {
        return tuples;
    }

    /**
     * 获取page页码
     * @return 页码
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * 通过调用预估大小函数，判断page是否还能插入行数组
     * @param candidate 待插入的行
     * @return 是否能插入
     * @throws IOException
     */
    public boolean isFull(Tuple candidate) throws IOException {
        int used = estimateCurrentSize();
        int added = candidate.toBytes().length;
        return used + added + 100 > PAGE_SIZE; // +100 留点空余防溢出
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private int estimateCurrentSize() throws IOException {
        int total = 8; // pageId(2) + rowCount(2)  + begin(2) + end(2)
        for (Tuple t : tuples) {
            total += t.toBytes().length;
        }
        return total;
    }

    // 序列化整页为 byte[]
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(PAGE_SIZE);
        DataOutputStream dataOut = new DataOutputStream(out);

        dataOut.writeInt(pageId);    // pageID
        dataOut.writeInt(tuples.size());     // 行数
        dataOut.writeShort(0);      // 真实数据开始位置的偏移量
        dataOut.writeShort(0);   // 真实数据结束位置的偏移量

        ArrayList<Integer> offsets = new ArrayList<>();
        int currentOffset = 2 + 2 * 2 +

        for (Tuple t : tuples) {
            byte[] rowBytes = t.toBytes();
            dataOut.write(rowBytes);
        }

        return out.toByteArray();
    }

    // 从 byte[] 反序列化
    public static Page fromBytes(byte[] data, Schema schema) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        int pageId = in.readShort();
        int rowCount = in.readShort();
        int begin = in.readShort();
        int end = in.readShort();

        ArrayList<Integer> offsets = new ArrayList<>(rowCount);
        for(int i = 0; i < rowCount; i++) {
            offsets.set(i, in.readInt());
        }

        Page page = new Page(pageId, schema);
        for (int i = 0; i < rowCount - 1; i++) {
            byte[] rowData = Arrays.copyOfRange(data, offsets.get(i), offsets.get(i+1));
            Tuple tuple = Tuple.fromBytes(rowData, schema);
            page.tuples.add(tuple);
        }

        return page;
    }

    public Schema getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return "Page#" + pageId + " rows=" + tuples.size();

    }
}
