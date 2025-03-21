package Operate;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class Schema {
    private String tableName;
    private Map<String,ColumnRule> columns;

    // 描述表的字段信息
    public static class ColumnRule{
        String type;
        boolean primaryKey = false;
        boolean notNull = false;

        // 后续可以添加更多约束
    }

    public Schema(String tableName,Map<String,ColumnRule>columns){
        this.tableName = tableName;
        this.columns = columns;
    }

    public static Schema loadSchema(String dbName,String tableName){
        File schemaFile = new File("../TestData/" + dbName + "/" + tableName + ".schema");

        //把.schema文件读成jsonObject，再转换成相应的java逻辑
        try(FileReader reader = new FileReader(schemaFile)){
            JsonObject schemaJson = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject columnsJson = schemaJson.getAsJsonObject("columns");

            Map<String,ColumnRule> columns = new HashMap<>();
            for(String colName:columnsJson.keySet()){
                JsonObject colRules = columnsJson.getAsJsonObject(colName);
                ColumnRule rule = new ColumnRule();
                rule.type = colRules.get("type").getAsString();
                if(colRules.has("notNull")) rule.notNull = colRules.get("notNull").getAsBoolean();

                columns.put(colName,rule);
            }
            return new Schema(tableName,columns);
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, ColumnRule> getColumns() {
        return columns;
    }

}
