package Util.Filter;

import Database.DatabaseManager;
import Table.Schema;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.Map;

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
}
