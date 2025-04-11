package Storage;

import Table.Schema;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Row {
    Map<String, Object> values;

    public Row() {
        values = new HashMap<>();
    }

    public void addValue(String column, Object value) {
        values.put(column,value);
    }

    public Object getValue(String column) {
        return values.get(column);
    }

    public byte[] toBytes(Schema schema) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);


        return buffer.array();
    }
}
