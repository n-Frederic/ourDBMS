package Conditions;

import Conditions.ConditionNode;

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
}
