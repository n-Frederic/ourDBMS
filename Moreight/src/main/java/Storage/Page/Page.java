package Storage.Page;

import Table.Schema;

import java.io.*;
import java.util.*;

public class Page {
    public static final int PAGE_SIZE = 8 * 1024; // 8KB

    private int pageId;    // page 的 id
    private Schema schema;
    private List<Tuple> tuples;

    public Page(int pageId, Schema schema) {
        this.pageId = pageId;
        this.schema = schema;
        this.tuples = new ArrayList<>();
    }

    public Page() {
        this.tuples = new ArrayList<>();
    }




    public Schema getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return "Page#" + pageId + " rows=" + tuples.size();
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
     * 将某一行插入page
     * @param tuple 待插入的行
     * @return 插入是否成功
     * @throws IOException
     */
    public boolean insert(Tuple tuple) throws IOException {
        if (isFull(tuple)) return false;
        for(int i = 0; i < tuples.size(); i++) {
            if(tuples.get(i).compare(tuple) > 0) {
                tuples.add(i,tuple);
                return true;
            }
        }
        tuples.add(tuple);
        return true;
    }

    /**
     * 在page的tuples里获取tuple
     * @param tuple tuple
     * @return
     */
    public Tuple get(Tuple tuple) {
        for (Tuple t : tuples) {
            if (t.compare(tuple) == 0) {
                return tuple;
            }
        }
        return null;
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
     * 估计当前页的大小
     * @return 总的字节数
     * @throws IOException
     */
    private int estimateCurrentSize() throws IOException {
        int total = 16;
        for (Tuple t : tuples) {
            total += t.toBytes().length;
        }
        return total;
    }






    /**
     * 页的结构如下:
     * 页id（int 4b）
     * 页的行数 （int 4b）
     * 真实数据开始的偏移量 begin (4b)
     * 真实数据结束的偏移量 end  (4b)
     * 每一行的开始的偏移量 (4b)
     * @return 字节数组
     * @throws IOException
     */
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(PAGE_SIZE);
        DataOutputStream dataOut = new DataOutputStream(out);

        dataOut.writeInt(pageId);    // pageID
        dataOut.writeInt(tuples.size());     // 行数
        dataOut.writeInt(0);      // 真实数据开始位置的偏移量，占位
        dataOut.writeInt(0);   // 真实数据结束位置的偏移量，占位

        int begin; // 真实数据开始位置的偏移量
        int end; // 真实数据结束位置的偏移量

        // 实时维护偏移量
        int currentOffset = 16;

        ArrayList<byte[]> info = new ArrayList<>();

        // 写入偏移量表
        for(Tuple tuple : tuples) {
            dataOut.writeInt(currentOffset);
            byte[] bytes = tuple.toBytes();
            info.add(bytes);
            currentOffset += 4;
        }

        // 此时偏移量在真实数据开始的位置
        begin = currentOffset;

        // 写入实际数据
        for (byte[] b : info) {
            dataOut.write(b);
            currentOffset += b.length;
        }

        // 此时偏移量在真实数据结束的位置
        end = currentOffset;

        byte[] bytes = out.toByteArray();
        // 写 begin 到 bytes[4] ~ bytes[7]
        bytes[4] = (byte) ((begin >> 24) & 0xFF);
        bytes[5] = (byte) ((begin >> 16) & 0xFF);
        bytes[6] = (byte) ((begin >> 8) & 0xFF);
        bytes[7] = (byte) (begin & 0xFF);

        // 写 end 到 bytes[8] ~ bytes[11]
        bytes[8]  = (byte) ((end >> 24) & 0xFF);
        bytes[9]  = (byte) ((end >> 16) & 0xFF);
        bytes[10] = (byte) ((end >> 8) & 0xFF);
        bytes[11] = (byte) (end & 0xFF);

        return bytes;
    }

    /**
     * 把bytes数组参考schema反序列化为Page
     * @param bytes 字节数组
     * @param schema 模式
     * @return 序列化好的page
     * @throws IOException
     */
    public static Page fromBytes(byte[] bytes, Schema schema) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        int pageId = in.readInt();
        int rowCount = in.readInt();
        int begin = ((bytes[4] & 0xFF) << 24) |
                ((bytes[5] & 0xFF) << 16) |
                ((bytes[6] & 0xFF) << 8) |
                (bytes[7] & 0xFF);

        int end = ((bytes[8] & 0xFF) << 24) |
                ((bytes[9] & 0xFF) << 16) |
                ((bytes[10] & 0xFF) << 8) |
                (bytes[11] & 0xFF);

        ArrayList<Integer> offsets = new ArrayList<>(rowCount);
        for(int i = 0; i < rowCount; i++) {
            offsets.set(i, in.readInt());
        }

        Page page = new Page(pageId, schema);
        for (int i = 0; i < rowCount - 1; i++) {
            int rowStart = offsets.get(i);
            int rowEnd = (i + 1 < rowCount) ? offsets.get(i + 1) : end;
            byte[] rowData = Arrays.copyOfRange(bytes, rowStart, rowEnd);
            Tuple tuple = Tuple.fromBytes(rowData, schema);
            page.tuples.add(tuple);
        }

        return page;
    }





}
