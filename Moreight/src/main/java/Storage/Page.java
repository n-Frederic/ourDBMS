package Storage;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

public class Page {
    public static final int PAGE_SIZE = 16 * 1024;
    public static final int HEADER_SIZE = 128;
    public static final int MAX_ROWS = (PAGE_SIZE - HEADER_SIZE) / 40;

    int pageId;
    int parentPageId;
    boolean isLeaf;
    int keyCount;
    List<Row> rows = new ArrayList<>();

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(PAGE_SIZE);
        buffer.putInt(pageId);           // offset 0
        buffer.putInt(parentPageId);     // offset 4
        buffer.put((byte) (isLeaf ? 1 : 0)); // offset 8
        buffer.putInt(keyCount);         // offset 9

        while (buffer.position() < HEADER_SIZE) {
            buffer.put((byte) 0);
        }

        for (Row row : rows) {
            buffer.put(row.toBytes());
        }

        while (buffer.position() < PAGE_SIZE) {
            buffer.put((byte) 0);
        }

        return buffer.array();
    }

    public void insertRow(Row row) {
        if (rows.size() >= MAX_ROWS) {
            throw new RuntimeException("Page is full");
        }
        rows.add(row);
        keyCount++;
    }


}
