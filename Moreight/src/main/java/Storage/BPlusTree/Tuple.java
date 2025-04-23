package Storage.BPlusTree;
import Storage.BPlusTree.Value.*;
import Conditions.Condition;

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
}
