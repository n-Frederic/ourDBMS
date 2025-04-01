package Operate;
import java.util.*;

public class LogicNode extends ExprNode {
    private final String operator; // AND or OR
    private final ExprNode left;
    private final ExprNode right;


    public LogicNode(String operator, ExprNode left, ExprNode right) {
        this.operator = operator.toUpperCase();
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Map<String, String> row) {
        switch (operator) {
            case "AND": return left.evaluate(row) && right.evaluate(row);
            case "OR": return left.evaluate(row) || right.evaluate(row);
            default: throw new RuntimeException("Unsupported logical operator: " + operator);
        }
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
