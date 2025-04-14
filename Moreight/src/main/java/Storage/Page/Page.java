package Storage.Page;

import Table.Schema;

import java.io.*;
import java.util.*;

public class Page {
    public static final int PAGE_SIZE = 16 * 1024; // 16KB

    private int pageId;    // page 的 id
    private Schema schema;
    private List<Row> rows;

    public Page(int pageId, Schema schema) {
        this.pageId = pageId;
        this.schema = schema;
        this.rows = new ArrayList<>();
    }

    public boolean insertRow(Row row) throws IOException {
        if (isFull(row)) return false;
        rows.add(row);
        return true;
    }

    public List<Row> getRows() {
        return rows;
    }

    public int getPageId() {
        return pageId;
    }

    public boolean isFull(Row candidate) throws IOException {
        int used = estimateCurrentSize();
        int added = candidate.toBytes().length;
        return used + added + 100 > PAGE_SIZE; // +100 留点空余防溢出
    }

    private int estimateCurrentSize() throws IOException {
        int total = 8; // pageId + rowCount (2 * 4 byte)
        for (Row r : rows) {
            total += r.toBytes().length;
        }
        return total;
    }

    // 序列化整页为 byte[]
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(PAGE_SIZE);
        DataOutputStream dataOut = new DataOutputStream(out);

        dataOut.writeInt(pageId);
        dataOut.writeInt(rows.size());

        for (Row r : rows) {
            byte[] rowBytes = r.toBytes();
            dataOut.writeInt(rowBytes.length);     // 每行前写长度
            dataOut.write(rowBytes);
        }

        return out.toByteArray();
    }

    // 从 byte[] 反序列化
    public static Page fromBytes(byte[] data, Schema schema) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        int pageId = in.readInt();
        int rowCount = in.readInt();

        Page page = new Page(pageId, schema);
        for (int i = 0; i < rowCount; i++) {
            int len = in.readInt();
            byte[] rowData = new byte[len];
            in.readFully(rowData);
            Row row = Row.fromBytes(rowData, schema);
            page.rows.add(row);
        }

        return page;
    }

    @Override
    public String toString() {
        return "Page#" + pageId + " rows=" + rows.size();
    }
}
