package Util.Filter;

import Database.DatabaseManager;
import Table.Schema;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Map;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TypeFilter {

    public static boolean typeMatch(String table, JsonObject newRow){

        Schema schema = Schema.loadSchema(DatabaseManager.getCurrentDatabase(), table);
        if (schema == null) {
            throw new RuntimeException("Schema not found for table: " + table);
        }

        for (Map.Entry<String, Schema.ColumnRule> entry : schema.getColumns().entrySet()) {
            String column = entry.getKey();
            Schema.ColumnRule columnRule = entry.getValue();
            String expectedType = columnRule.getType();

            JsonElement element = newRow.get(column);

            // 判断是否为空
            if (element == null || element.isJsonNull() || element.getAsString().isEmpty()) {
                if (columnRule.isNotNull()) {
                    throw new IllegalArgumentException("Column '" + column + "' cannot be null.");
                } else if (!columnRule.getDefaultValue().isEmpty()) {
                    // 用默认值填充
                    newRow.addProperty(column, columnRule.getDefaultValue());
                } else {
                    newRow.add(column, JsonNull.INSTANCE);
                }
                continue;
            }
            // 类型校验
            if (!isValidType(element, expectedType)) {
                throw new IllegalArgumentException("Invalid data type for column '" + column + "'. Expected: " + expectedType);
            }

            // 唯一性校验
            if (columnRule.isUnique()) {
                if (!isValueUnique(table, column, element.getAsString())) {
                    throw new IllegalArgumentException("Value for column '" + column + "' must be unique.");
                }
            }

        }
        return true;
    }


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

    private static boolean isValueUnique(String table, String column, String value) {
        Path filePath = Paths.get(DatabaseManager.DIRECTORY, DatabaseManager.getCurrentDatabase(), table + ".json");
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
