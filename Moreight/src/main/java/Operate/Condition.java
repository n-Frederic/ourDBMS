package Operate;

public class Condition {
    private String column;
    private Object value;
    private String operator;

    public Condition(String column, Object value, String operator) {
        this.column = column;
        this.value = value;
        this.operator = operator;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "column=" + column + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }
}
