package Conditions;
import Table.Table;
import com.google.gson.JsonArray;
import java.util.*;
import java.util.Map;
import java.nio.file.Path;

public class Condition extends ConditionNode {
    Path tablepath;
    private String column;
    private String value;
    private String operator;

    public void setTablepath(Path tablepath){
        this.tablepath=tablepath;

    }

    public Condition(String column, String value, String operator,Path filepath) {
        this.column = column;
        this.value = value;
        this.operator = operator;
        this.tablepath=filepath;
    }

    public Condition(String column, String value, String operator) {
        this.column = column;
        this.value = value;
        this.operator = operator;
    }


    public String getColumn() {
        return column;
    }

    public String getValue() {
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
    public JsonArray evaluate() {

        JsonArray data=Table.readData(tablepath);
        JsonArray result= Table.Where(data,this);
        return result;
        // 根据操作符进行比较，这里仅示例了部分操作符

    }
}


