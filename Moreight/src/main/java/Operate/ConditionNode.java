package Operate;
import java.util.*;

public class ConditionNode extends ExprNode {
    private final Condition condition;

    public ConditionNode(Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Map<String, String> row) {
        String actual = row.get(condition.getColumn());
        String expected = condition.getValue();
        String op = condition.getOperator();

        if (actual == null) return false;

        switch (op) {
            case "=": return actual.equals(expected);
            case "!=": return !actual.equals(expected);
            case ">": return Integer.parseInt(actual) > Integer.parseInt(expected);
            case "<": return Integer.parseInt(actual) < Integer.parseInt(expected);
            case ">=": return Integer.parseInt(actual) >= Integer.parseInt(expected);
            case "<=": return Integer.parseInt(actual) <= Integer.parseInt(expected);
            // 其他类型如 LIKE/IN 可自行扩展
            default: return false;
        }
    }

    @Override
    public String toString() {
        return condition.toString();
    }
}
