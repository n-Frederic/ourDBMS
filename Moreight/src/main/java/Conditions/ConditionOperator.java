package Conditions;
import java.util.Map;

import Conditions.ConditionNode;
import Table.Table;
import com.google.gson.JsonArray;

public class ConditionOperator extends ConditionNode {
    private final String operator; // "AND" or "OR"
    private final ConditionNode left;
    private final ConditionNode right;

    public ConditionOperator(String operator, ConditionNode left, ConditionNode right) {
        this.operator = operator.toUpperCase();
        this.left = left;
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public ConditionNode getLeft() {
        return left;
    }

    public ConditionNode getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }

    @Override
    public JsonArray evaluate() {
        JsonArray leftResult = left.evaluate();
        JsonArray rightResult = right.evaluate();
        return Table.Where(leftResult,rightResult,this.operator);


    }

    public JsonArray evaluate(JsonArray data) {
        JsonArray leftResult = left.evaluate(data);
        JsonArray rightResult = right.evaluate(data);
        return Table.Where(leftResult,rightResult,this.operator);


    }
}

