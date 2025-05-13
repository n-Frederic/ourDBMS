package Util.Filter;
//import Storage.BPlusTree.Value.*;
import Storage.Value.Value;
import Table.*;
import Parser.commandParser;
import java.util.*;
import java.util.ArrayList;
import Database.DatabaseManager;
import Table.Schema;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.util.Map;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * TypeFilter类用于验证插入到数据库表中的数据是否符合表的结构定义。
 * 它确保数据类型正确、处理空值和默认值，并检查唯一性约束。
 */
public class TypeFilter {

//    /**
//     * 验证插入的数据是否符合表的结构定义。
//     * @param table 表名。
//     * @param newRow 包含要插入数据的JSON对象。
//     * @return 如果数据验证成功，返回true；否则抛出异常。
//     * @throws RuntimeException 如果表的结构定义不存在。
//     * @throws IllegalArgumentException 如果数据不符合表的结构定义。
//     */
//    public static boolean typeMatch(Table table, JsonObject newRow){
//
//        Schema schema = table.getSchema();
//        if (schema == null) {
//            throw new RuntimeException("Schema not found for table: " + table);
//        }
//
//        for (Map.Entry<String, Schema.ColumnRule> entry : schema.getFields().entrySet()) {
//            String column = entry.getKey();
//            Schema.ColumnRule columnRule = entry.getValue();
//            String expectedType = columnRule.getType();
//
//            JsonElement element = newRow.get(column);
//
//            // 判断是否为空
//            if (element == null || element.isJsonNull() || element.getAsString().isEmpty()) {
//                if (columnRule.isNotNull()) {
//                    throw new IllegalArgumentException("Column '" + column + "' cannot be null.");
//                } else if (!columnRule.getDefaultValue().isEmpty()) {
//                    // 用默认值填充
//                    newRow.addProperty(column, columnRule.getDefaultValue());
//                } else {
//                    newRow.add(column, JsonNull.INSTANCE);
//                }
//                continue;
//            }
//            // 类型校验
//            if (!isValidType(element, expectedType)) {
//                throw new IllegalArgumentException("Invalid data type for column '" + column + "'. Expected: " + expectedType);
//            }
//
//            // 唯一性校验
//            if (columnRule.isUnique()) {
//                if (!isValueUnique(table, column, element.getAsString())) {
//                    throw new IllegalArgumentException("Value for column '" + column + "' must be unique.");
//                }
//            }
//
//        }
//        return true;
//    }

    public static boolean tableExist(String tableName){
        List<String>tables=TableManager.showTables();
        for(String table:tables){
            if(tableName==table)return true;
        }
        return false;
    }

    public static boolean databaseExist(String dbName){
        List<String>dbs= DatabaseManager.listDatabases();
        for(String db:dbs){
            if(db.equals(dbName))return true;
        }
        return false;
    }

    public static boolean columnExist(Table table,String column) {
        Schema schema = table.getSchema();
        Field field=schema.getField(column);
        return field != null;
    }

    public static boolean typeExist(ArrayList<Field> field){
        for(Field field1:field){
            String type=field1.getType();
            return type.equals("string") || type.equals("Integer") || type == "long" || type == "boolean";
        }
        return false;
    }


    /**
     * 验证值的类型是否符合期望的类型。
     * @param value 要验证的值。
     * @param expectedType 期望的类型（如"String"、"Integer"等）。
     * @return 如果值的类型符合期望的类型，返回true；否则返回false。
     */
    public static boolean isValidType(Object value, String expectedType) {
        if (value == null) {
            return !expectedType.equals("String"); // 根据需要的类型判断是否允许 null
        }

        return switch (expectedType) {
            case "String" -> value instanceof String;
            case "Integer" -> value instanceof Integer;
            case "Double" -> value instanceof Double;
            case "Boolean" -> value instanceof Boolean;
            // 后续可以添加更多类型
            default -> false;
        };
    }

//    public static boolean columnUnique(ArrayList<Field> field){
//
//    }





    public static List<Value> validateAndConvertValues(
            List<String> columns,
            List<Object> rawValues,
            Schema schema) {

        List<Field> fields = schema.getFields();
        List<Value> columnTypes=new ArrayList();
        for(int i=0;i<fields.size();i++){
//            Class<? extends Value> value=Value.parse(commandParser.findClass(schema.getField(fields.get(i).getName()).getType()),schema.getField(fields.get(i).getName()).getType());

            columnTypes.add(i,Value.parse(commandParser.findClass(schema.getField(fields.get(i).getName()).getType()),schema.getField(fields.get(i).getName()).getType()));

        }

        // 构建“列名 → 下标”映射
        Map<String,Integer> colIdxMap = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            colIdxMap.put(fields.get(i).getName().toLowerCase(), i);
        }

        // 列和值数量必须一致
        if (columns.size() != rawValues.size()) {
            throw new IllegalArgumentException(
                    "INSERT 列和值数量不匹配：列 " + columns.size() + " vs 值 " + rawValues.size());
        }

        List<Value> castedValues = new ArrayList<>(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            String col = columns.get(i).toLowerCase();
            Object raw = rawValues.get(i);

            Integer idx = colIdxMap.get(col);
            if (idx == null) {
                throw new IllegalArgumentException(
                        "列 `" + col + "` 在表中不存在");
            }
            //Class<Value> expectedType = columnTypes.get(idx);
            Value v;
            if (raw instanceof Value) {
                v = (Value) raw;
            } else {
                System.out.println("");
                return null;
//                // 按照预期类型做一次转换
//                v = ValueParser.parse(raw.toString(), expectedType);
            }

//            if (!expectedType.isInstance(v)) {
//                throw new IllegalArgumentException(
//                        String.format("插入值类型不匹配：列 `%s` 期望 %s，实际 %s",
//                                col,
//                                expectedType.getSimpleName(),
//                                v.getClass().getSimpleName()));
//            }

            castedValues.add(v);
        }

        return castedValues;
    }
    private static boolean isValueUnique(String table, String column, String value) {
        Path filePath = Paths.get("../TestData", "DatabaseManager", DatabaseManager.getCurrentDatabase(), table + ".json");
        if (!Files.exists(filePath)) {
            // 如果文件不存在，说明表为空，值是唯一的
            return true;
        }

        try (FileReader reader = new FileReader(filePath.toFile())) {
            Gson gson = new Gson();
            JsonArray data = gson.fromJson(reader, JsonArray.class);

            for (int i = 0; i < data.size(); i++) {
                JsonObject row = data.get(i).getAsJsonObject();
                if (row.has(column)) {
                    String currentValue = row.get(column).getAsString();
                    if (currentValue.equals(value)) {
                        return false; // 找到重复值
                    }
                }
            }
            return true; // 没有找到重复值
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}