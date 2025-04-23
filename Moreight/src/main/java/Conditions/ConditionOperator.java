package Conditions;
import java.util.ArrayList;
import java.util.Map;

import Conditions.ConditionNode;
import Storage.BPlusTree.Tuple;
import Table.Table;
import com.google.gson.JsonArray;

public class ConditionOperator extends ConditionNode {
    private final String operator; // "AND" or "OR"
    private Table table;
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
    public ArrayList<Tuple>  evaluate() {
        ArrayList<Tuple> leftResult = left.evaluate();
        ArrayList<Tuple>rightResult = right.evaluate();

        return table.where(leftResult,rightResult,this.operator);



    }

//    public  ArrayList<Tuple> evaluate(ArrayList<Tuple> data) {
//        ArrayList<Tuple> leftResult = left.evaluate(data);
//        ArrayList<Tuple> rightResult = right.evaluate(data);
//        return Table.Where(leftResult,rightResult,this.operator);
//
//
//    }
}

