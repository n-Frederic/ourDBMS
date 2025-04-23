package Storage.BPlusTree.Value;

/**
 * Created by zhangtianlong on 17/10/15.
 */
public abstract class Value {

    public static final byte UNKNOW = 100;
    public static final byte NULL = 0;
    public static final byte STRING = 1;
    public static final byte INT = 2;
    public static final byte LONG = 3;
    public static final byte BOOLEAN = 4;

    public abstract int getLength();

    public abstract byte getType();

    public abstract int compare(Value value);
    // 根据类型字符串和字符串值解析成具体子类实例
    public static Value parse(String typeName, String literalValue) {
        return switch (typeName.toLowerCase()) {
            case "int", "integer" -> new IntValue(Integer.parseInt(literalValue));
            case "string" -> new StringValue(literalValue);
            case "boolean" -> new BooleanValue(Boolean.parseBoolean(literalValue));
            default -> throw new IllegalArgumentException("Unknown type: " + typeName);
        };
    }
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


}
