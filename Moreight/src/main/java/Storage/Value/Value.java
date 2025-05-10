package Storage.Value;
import java.util.ArrayList; // 单独导入ArrayList
import java.util.*;        // 导入java.util下的所有类（不推荐，可能引起命名冲突）

public abstract class Value {

    public static final byte UNKNOWN = 100;
    public static final byte NULL = 5;
    public static final byte STRING = 1;
    public static final byte INT = 2;
    public static final byte LONG = 3;
    public static final byte BOOLEAN = 4;

    public abstract int getLength();

    public abstract byte getType();

    public abstract int compare(Value value);
    // 根据类型字符串和字符串值解析成具体子类实例

    /**
     * 把字符串类型，判断实际类型后，转化成Value的子类
     * @param clazz
     * @param literalValue
     * @return
     */
    public static Value parse(Class<? extends Value> clazz, String literalValue) {
        if (clazz == IntValue.class) {
            return new IntValue(Integer.parseInt(literalValue));
        }  else if (clazz == StringValue.class) {
            return new StringValue(literalValue);
        } else if (clazz == BooleanValue.class) {
            return new BooleanValue(Boolean.parseBoolean(literalValue));
        } else {
            throw new IllegalArgumentException("Unsupported class type: " + clazz.getName());
        }
    }
    public static ArrayList<Value> sortValues(ArrayList<Value> values) {
        // 创建列表副本以避免修改原列表
        ArrayList<Value> sorted = new ArrayList<>(values);

        // 使用Value自身的compare方法进行比较
        Collections.sort(sorted, new Comparator<Value>() {
            @Override
            public int compare(Value v1, Value v2) {
                return v1.compare(v2); // 直接调用Value类的compare方法
            }
        });

        return sorted;
    }

    public abstract Object getValue();

    public abstract byte[] toBytes();


}
