package Operate;

import com.google.gson.*;

import java.io.*;
import java.util.*;

public class Schema {
    private String tableName;
    private Map<String, ColumnRule> columns;

    // 描述表的字段信息
    public static class ColumnRule {
        String type;
        boolean primaryKey = false;
        boolean notNull = false;
        String defaultValue = "";
        // Integer min = null;
        // Integer max = null;

        // 后续可以添加更多约束
    }

    public Schema(String tableName, Map<String, ColumnRule> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public static Schema loadSchema(String dbName, String tableName) {
        File schemaFile = new File("../TestData/" + dbName + "/" + tableName + ".schema");

        try (FileReader reader = new FileReader(schemaFile)) {
            JsonArray schemaArray = JsonParser.parseReader(reader).getAsJsonArray();  // 读取成数组

            Map<String, ColumnRule> columns = new HashMap<>();

            for (JsonElement colElement : schemaArray) {
                JsonObject colObject = colElement.getAsJsonObject();
                String colName = colObject.get("name").getAsString();  // 获取字段名

                ColumnRule rule = new ColumnRule();
                JsonArray constraints = colObject.getAsJsonArray("constraint");

                for (JsonElement constraintElement : constraints) {
                    JsonObject constraint = constraintElement.getAsJsonObject();

                    // 解析各种约束
                    if (constraint.has("Type")) rule.type = constraint.get("Type").getAsString();
                    if (constraint.has("PRIMARY KEY")) rule.primaryKey = constraint.get("PRIMARY KEY").getAsBoolean();
                    if (constraint.has("UNIQUE")) ;
                    if (constraint.has("NOT NULL")) rule.notNull = constraint.get("NOT NULL").getAsBoolean();
                    if (constraint.has("Default")) rule.defaultValue = constraint.get("Default").getAsString();
                }
                columns.put(colName, rule);
            }
            return new Schema(tableName, columns);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
