package Conditions;
import Storage.BPlusTree.Tuple;
import Storage.BPlusTree.Value.Value;
import Table.Table;
import com.google.gson.JsonArray;
import java.util.*;
import java.util.Map;
import java.nio.file.Path;
import Storage.*;

public class Condition extends ConditionNode {
    Table table;
    private String column;
    private Value value;
    private String operator;

    public void setTable(Table table){
        this.table=table;

    }

    public Condition(String column, Value value, String operator,Table table) {
        this.column = column;
        this.value = value;
        this.operator = operator;
        this.table=table;
    }

    public Condition(String column, Value value, String operator) {
        this.column = column;
        this.value = value;
        this.operator = operator;
    }


    public String getColumn() {
        return column;
    }

    public Value getValue() {
        return value;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "column=" + column  +
                ", operator=" + operator +
                ", value=" + value +
                "}";
    }

    @Override
    public  ArrayList<Tuple> evaluate() {



        ArrayList<Tuple>tuples=table.where(this);
        return tuples;


    }
//    public  ArrayList<Tuple> evaluate(JsonArray data) {
//
//        //JsonArray data=Table.readData(tablepath);
//        Table.Where(data,this);
//        return data;
//
//
//    }
}


