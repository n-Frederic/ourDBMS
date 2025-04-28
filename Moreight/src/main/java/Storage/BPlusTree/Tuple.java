package Storage.BPlusTree;
import Storage.BPlusTree.Value.*;
import Conditions.Condition;
import Storage.Page.Row;
import Table.Field;
import Table.Schema;

import java.io.*;
import java.util.List;

/**
 * 元组
 *
 * @author zhangtianlong
 */
public class Tuple {

    protected Value[] values;

    public Tuple() {
    }

    public Tuple(Value[] values) {
        this.values = values;
    }

    public Value[] getValues() {
        return values;
    }

    public Value getValue(int index) { return values[index];}

    public void setValues(Value[] values) {
        this.values = values;
    }

    public int getLength() {
        int sum = 0;
        for (Value value : values) {
            sum += value.getLength();
        }
        return sum;
    }

    /**
     * 联合索引比较的时候,先比较第一个索引值,若相等则再比较下一个索引值,依次类推
     * TODO ：更改为比较主键
     */
    public int compare(Tuple tuple) {
        int min = Math.min(values.length, tuple.values.length);
        for (int i = 0; i < min; i++) {
            int comp = values[i].compare(tuple.values[i]);
            if (comp == 0) {
                continue;
            }
            return comp;
        }
        int res = values.length - tuple.values.length;
        return (res == 0) ? 0 : (res > 1 ? 1 : -1);
    }

    public void set(int index, Value value) {
        values[index] = value;
    }

    public boolean check(Condition condition, int index) {
        switch (condition.getOperator()) {
            case "=":
                if (values[index].compare(condition.getValue())==0) {
                    return true;
                } else return false;
            case "!=":
                if (values[index].compare(condition.getValue())!=0) {
                    return true;
                } else return false;
            case "<":
                if (values[index].compare(condition.getValue()) == -1) {
                    return true;
                } else return false;
            case ">":
                if (values[index].compare(condition.getValue()) == 1) {
                    return true;
                } else return false;
            case "<=":
                if (values[index].compare(condition.getValue()) <= 0) {
                    return true;
                } else return false;
            case ">=":
                if (values[index].compare(condition.getValue()) >= 0) {
                    return true;
                } else return false;
        }
        return false;
    }

    // 添加一个值到末尾
    public void appendValue(Value value) {
        Value[] newValues = new Value[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[values.length] = value;
        this.values = newValues;
    }

    // 根据列号移除一个值（比如删除一列的时候用）
    public void removeValue(int index) {
        if (index < 0 || index >= values.length) return;
        Value[] newValues = new Value[values.length - 1];
        for (int i = 0, j = 0; i < values.length; i++) {
            if (i != index) {
                newValues[j++] = values[i];
            }
        }
        this.values = newValues;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);

        for (Value value : values) {
            writeTypedValue(dataOut, value);
        }
        
        return out.toByteArray();
    }

    public static Tuple fromBytes(byte[] data, Schema schema) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        List<Field> fields = schema.getFields();
        Value[] values = new Value[fields.size()];

        for (int i = 0; i < fields.size(); i++) {
            int len = in.readInt();
            byte[] valueDate = new byte[len];
            in.readFully(valueDate);
            // values[i]都转化成对应类型的方法？
        }
        return new Tuple(values);
    }

    private void writeTypedValue(DataOutputStream out, Value value) throws IOException {
        switch (value.getType()) {
            case 1:
                out.writeUTF((String) value.getValue());
                break;
            case 2:
                out.writeInt((Integer) value.getValue());
                break;
            case 3:
                out.writeLong((Long) value.getValue());
                break;
            case 4:
                out.writeBoolean((Boolean) value.getValue());
                break;
            default:
                throw new IOException("未知类型");
        }
    }
}
