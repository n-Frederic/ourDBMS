package Storage.Page;

import Table.Field;
import Table.Schema;

import java.util.*;
import java.io.*;

public class Row implements Comparable<Row>{
    private Schema schema;                          // 表结构
    private Map<String, Object> values;             // 字段名 → 值



    public Row(Schema schema) {
        this.schema = schema;
        this.values = new LinkedHashMap<>();        // 保证字段顺序
        initializeWithDefaults();
    }

    // 初始化默认值
    private void initializeWithDefaults() {
        for (Field field : schema.getFields()) {
            String name = field.getName();
            String def = field.getDefault();

            if (def != null) {
                values.put(name, parseValue(field.getType(), def));
            } else {
                values.put(name, null);
            }
        }
    }



    public void setValue(String fieldName, Object value) {
        Field field = schema.getField(fieldName);
        if (field == null) throw new IllegalArgumentException("字段不存在: " + fieldName);

        if (value == null && field.isNotNull()) {
            throw new IllegalArgumentException("字段 " + fieldName + " 不允许为 NULL");
        }

        // 类型转换（可扩展：目前只处理基本类型）
        Object converted = parseValue(field.getType(), value);
        values.put(fieldName, converted);
    }

    public Object getValue(String fieldName) {
        return values.get(fieldName);
    }

    public Schema getSchema() {
        return schema;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    // 简单序列化为字节数组（类型信息不包含在内，依赖 schema 顺序）
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);

        for (Field field : schema.getFields()) {
            Object value = values.get(field.getName());

            if (value == null) {
                dataOut.writeBoolean(false);
            } else {
                dataOut.writeBoolean(true);
                writeTypedValue(dataOut, field.getType(), value);
            }
        }

        return out.toByteArray();
    }

    public static Row fromBytes(byte[] data, Schema schema) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        Row row = new Row(schema);

        for (Field field : schema.getFields()) {
            String type = field.getType();
            boolean hasValue = in.readBoolean();
            if (hasValue) {
                Object val = readTypedValue(in, type);
                row.setValue(field.getName(), val);
            } else {
                row.setValue(field.getName(), null);
            }
        }

        return row;
    }

    @Override
    public int compareTo(Row other) {
        String primaryKey = schema.getPrimaryKeyName();

        // 获取当前 Row 和其他 Row 的主键值（根据列名动态获取）
        Object thisValue = values.get(primaryKey);
        Object otherValue = other.getValues().get(primaryKey);

        // 检查这两个主键值的类型，并进行相应的比较
        if (thisValue instanceof Comparable && otherValue instanceof Comparable) {
            // 如果两者都是 Comparable 类型的，进行比较
            Comparable thisComparable = (Comparable) thisValue;
            Comparable otherComparable = (Comparable) otherValue;

            // 使用主键进行比较，返回排序结果
            return thisComparable.compareTo(otherComparable);
        } else {
            // 如果类型不匹配或者不可比较，抛出异常
            throw new IllegalArgumentException("The primary key columns are not comparable.");
        }
    }

    // ---------- 辅助函数 ----------

    private Object parseValue(String type, Object value) {
        if (value == null) return null;
        switch (type.toLowerCase()) {
            case "int": return Integer.parseInt(value.toString());
            case "double": return Double.parseDouble(value.toString());
            case "string": return value.toString();
            default: throw new IllegalArgumentException("不支持的类型: " + type);
        }
    }

    private void writeTypedValue(DataOutputStream out, String type, Object value) throws IOException {
        switch (type.toLowerCase()) {
            case "int":
                out.writeInt((Integer) value);
                break;
            case "double":
                out.writeDouble((Double) value);
                break;
            case "string":
                out.writeUTF((String) value);
                break;
            default:
                throw new IOException("未知类型: " + type);
        }
    }

    private static Object readTypedValue(DataInputStream in, String type) throws IOException {
        switch (type.toLowerCase()) {
            case "int": return in.readInt();
            case "double": return in.readDouble();
            case "string": return in.readUTF();
            default: throw new IOException("未知类型: " + type);
        }
    }

    @Override
    public String toString() {
        return values.toString();
    }
}

