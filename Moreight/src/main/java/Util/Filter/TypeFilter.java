package Util.Filter;

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

    /**
     * 验证插入的数据是否符合表的结构定义。
     * @param table 表名。
     * @param newRow 包含要插入数据的JSON对象。
     * @return 如果数据验证成功，返回true；否则抛出异常。
     * @throws RuntimeException 如果表的结构定义不存在。
     * @throws IllegalArgumentException 如果数据不符合表的结构定义。
     */
//    public static boolean typeMatch(String table, JsonObject newRow){
//
//        Schema schema = Schema.loadSchema(DatabaseManager.getCurrentDatabase(), table);
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


    /**
     * 验证值的类型是否符合期望的类型。
     * @param value 要验证的值。
     * @param expectedType 期望的类型（如"String"、"Integer"等）。
     * @return 如果值的类型符合期望的类型，返回true；否则返回false。
     */
    private static boolean isValidType(Object value, String expectedType) {
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

    /**
     * 检查值在表的列中是否唯一。
     * @param table 表名。
     * @param column 列名。
     * @param value 要检查的值。
     * @return 如果值在列中唯一，返回true；否则返回false。
     */
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
