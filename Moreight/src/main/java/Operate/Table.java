package Operate;

import Conditions.Condition;
import Function.DatabaseManager;
import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public class Table {
    private static final String DIRECTORY = "../TestData/DatabaseManager";

    public static void InsertIntoValue(String table, ArrayList<String> columns, ArrayList<Object> values) {
        try {
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");
            JsonArray dataArray;

            if (Files.exists(dataPath)) {
                FileReader reader = new FileReader(dataPath.toFile());
                dataArray = JsonParser.parseReader(reader).getAsJsonArray();
            } else {
                dataArray = new JsonArray();
            }

            Schema schema = Schema.loadSchema(DatabaseManager.getCurrentDatabase(), table);
            if (schema == null) {
                throw new RuntimeException("Schema not found for table: " + table);
            }

            JsonObject newRow = new JsonObject();
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                Object value = values.get(i);

                Schema.ColumnRule columnRule = schema.getColumn(column);
                // 校验值的类型(正在试，还在改)
                String expectedType = columnRule.getType();
                if (!isValidType(value, expectedType)) {
                    throw new IllegalArgumentException("Invalid data type for column " + column);
                }

                newRow.addProperty(column, value.toString());
            }


//            for (int i = 0; i < columns.size(); i++) {
//                String column = columns.get(i);
//                Object value = values.get(i);

//                switch (value) {
//                    case String s -> newRow.addProperty(column, s);
//                    case Integer integer -> newRow.addProperty(column, integer);
//                    case Double v -> newRow.addProperty(column, v);
//                    case Boolean b -> newRow.addProperty(column, b);
//                    case null, default ->
//                            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
//                }
//            }

            dataArray.add(newRow);

            try (FileWriter writer = new FileWriter(dataPath.toFile())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(dataArray, writer);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // update 是一个总的关于update from ... set ... 的调用，也就是传入where筛选后的JsonArray
    public static void Update(String table, JsonArray data, Map<String, String> newValues) {
        try {
            Path dataPath = From_data(table);

            if (!Files.exists(dataPath)) {
                System.out.println("Table data file does not exist.");
                return;
            }

            FileReader reader = new FileReader(dataPath.toFile());
            JsonArray dataArray = JsonParser.parseReader(reader).getAsJsonArray();

            if (dataArray == null || dataArray.isEmpty()) {
                System.out.println("No data to update.");
                return;
            }





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // set是针对于where筛选后的JsonArray修改列值
    public static JsonArray Set(JsonArray data, HashMap<String,String> map) {
        Iterator<JsonElement> iterator = data.iterator();

        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            JsonObject object = element.getAsJsonObject();

            for(String key : map.keySet()) {
                object.addProperty(key,map.get(key));
            }
        }
        return data;
    }


    public static void Delete(String table, JsonArray data) {
        try {
            Path schemaPath = From_schema(table);
            Path dataPath = From_data(table);
            if (!Files.exists(schemaPath)) {
                System.out.println("Table data file does not exist.");
                return;
            }

            FileReader reader = new FileReader(schemaPath.toFile());
            JsonObject schemaJson = JsonParser.parseReader(reader).getAsJsonObject();

            if (schemaJson == null || !schemaJson.has("fields")) {
                System.out.println("Invalid schema or no fields found.");
                return;
            }
            JsonArray fieldsArray = schemaJson.getAsJsonArray("fields");


            JsonArray newFieldsArray = new JsonArray();

            // 遍历 fieldsArray，删除匹配的记录
            for (JsonElement element : fieldsArray) {
                // 如果 element 不在 data 中，则保留该记录
                if (!data.contains(element)) {
                    newFieldsArray.add(element);
                }
            }

            // 如果没有匹配的记录，提示用户
            if (newFieldsArray.size() == fieldsArray.size()) {
                System.out.println("No matching records found for deletion.");
                return;
            }

            schemaJson.add("fields", newFieldsArray);
            // 写回 JSON 文件
            try (FileWriter writer = new FileWriter(dataPath.toFile())) {
                gson.toJson(schemaJson, writer);
            }
            System.out.println("Deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path From_data(String table) {
        Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");
        return dataPath;
    }

    public static Path From_schema(String table) {
        Path schemaPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_schema.json");
        return schemaPath;
    }

    public static JsonArray Where(JsonArray a, JsonArray b, String mode) {
        JsonArray array = new JsonArray();
        if (mode.equals("and")) {
            for (JsonElement e : a) {
                if (b.contains(e)) {
                    array.add(e);
                }
            }
            return array;
        } else if (mode.equals("or")) {
            for (JsonElement e : a) {
                array.add(e);
            }
            for (JsonElement e : b) {
                if (!array.contains(e)) {
                    array.add(e);
                }
            }
            return array;
        } else return array;
    }



    public static JsonArray Where(Path dataPath, Condition condition) {
        JsonArray data = new JsonArray();
        try (FileReader reader = new FileReader(dataPath.toFile())) {
            data = JsonParser.parseReader(reader).getAsJsonArray();
            data = DealWithArray(data, condition);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }





















    public static JsonArray DealWithArray(JsonArray data, Condition condition) {
        Iterator<JsonElement> iterator = data.iterator();

        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            JsonObject object = element.getAsJsonObject();

            switch (condition.getOperator()) {
                case "=":
                    if (!object.get(condition.getColumn()).getAsString().equals(condition.getValue())) {
                        iterator.remove();
                    }
                    break;
                case "!=":
                    if (object.get(condition.getColumn()).getAsString().equals(condition.getValue())) {
                        iterator.remove();
                    }
                    break;
                case "<":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) >= 0) {
                        iterator.remove();
                    }
                    break;
                case ">":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) <= 0) {
                        iterator.remove();
                    }
                    break;
                case "<=":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) > 0) {
                        iterator.remove();
                    }
                    break;
                case ">=":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) < 0) {
                        iterator.remove();
                    }
                    break;
            }
        }

        return data;
    }

    private static boolean isValidType(Object value, String expectedType) {
        if (value == null) {
            return !expectedType.equals("String"); // 根据需要的类型判断是否允许 null
        }

        switch (expectedType) {
            case "String":
                return value instanceof String;
            case "Integer":
                return value instanceof Integer;
            case "Double":
                return value instanceof Double;
            case "Boolean":
                return value instanceof Boolean;
            // 后续可以添加更多类型
            default:
                return false;
        }
    }

    private static boolean isWideChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    private static int getDisplayWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            if (isWideChar(c)) {
                width += 2;
            } else {
                width += 1;
            }
        }
        return width;
    }

    private static String padRight(String str, int displayWidth) {
        int currentWidth = getDisplayWidth(str);
        if (currentWidth >= displayWidth) {
            return str;
        }

        int spaceCount = displayWidth - currentWidth;
        return str + " ".repeat(spaceCount);
    }

    public static void DrawSelectedTable(JsonArray data, ArrayList<String> columns, Map<String, Integer> columnWidths) {
        if (columns.isEmpty()) {
            for (var rowElement : data) {
                JsonObject row = rowElement.getAsJsonObject();
                columns.addAll(row.keySet());
            }
        }

        for (String column : columns) {
            columnWidths.put(column, column.length());
        }
        for (var rowElement : data) {
            JsonObject row = rowElement.getAsJsonObject();
            for (String col : columns) {
                String val = row.has(col) ? row.get(col).getAsString() : "";
                columnWidths.put(col, Math.max(columnWidths.get(col), getDisplayWidth(val)));
            }
        }

        Runnable printSeparator = () -> {
            System.out.print("+");
            for (String col : columns) {
                int width = columnWidths.get(col);
                System.out.print("-".repeat(width + 2) + "+");
            }
            System.out.println();
        };

        Consumer<Map<String, String>> printRow = rowMap -> {
            System.out.print("|");
            for (String col : columns) {
                String val = rowMap.getOrDefault(col, "").replace("\t", "    ");
                int width = columnWidths.get(col);
                System.out.print(" " + padRight(val, width) + " |");
            }
            System.out.println();
        };

        printSeparator.run();
        Map<String, String> headerMap = new LinkedHashMap<>();
        for (String col : columns) headerMap.put(col, col);
        printRow.accept(headerMap);
        printSeparator.run();

        for (var rowElement : data) {
            JsonObject row = rowElement.getAsJsonObject();
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (String col : columns) {
                String val = row.has(col) ? row.get(col).getAsString() : "";
                rowMap.put(col, val);
            }
            printRow.accept(rowMap);
        }
        printSeparator.run();

    }


//    private static ArrayList<JsonElement> JsonArrayToArrayList(JsonArray jsonArray) {
//        ArrayList<JsonElement> array = new ArrayList<>();
//        for (JsonElement element : jsonArray) {
//            array.add(element);
//        }
//        return array;
//    }
//
//    private static JsonArray ArrayListToJsonArray(ArrayList<JsonElement> arrayList) {
//        JsonArray array = new JsonArray();
//        for (JsonElement element : arrayList) {
//            array.add(element);
//        }
//        return array;
//    }


//    public static JsonArray selectColumns(JsonArray originalArray, ArrayList<String> columns) {
//        JsonArray resultArray = new JsonArray();
//
//        for (int i = 0; i < originalArray.size(); i++) {
//            JsonObject originalObj = originalArray.get(i).getAsJsonObject();
//            JsonObject filteredObj = new JsonObject();
//
//            for (String column : columns) {
//                if (originalObj.has(column)) {
//                    filteredObj.add(column, originalObj.get(column));
//                }
//            }
//
//            resultArray.add(filteredObj);
//        }
//
//        return resultArray;
//    }

    //    private static boolean compare(JsonElement rowValue, String value, String operator) {
//        if (rowValue.isJsonPrimitive()) {
//            JsonPrimitive primitive = rowValue.getAsJsonPrimitive();
//
//            if (primitive.isNumber()) {
//                double rowNum = primitive.getAsDouble();
//                double targetNum = Double.parseDouble(value);
//
//                switch (operator) {
//                    case "=":
//                        return rowNum == targetNum;
//                    case "!=":
//                        return rowNum != targetNum;
//                    case ">":
//                        return rowNum > targetNum;
//                    case "<":
//                        return rowNum < targetNum;
//                    case ">=":
//                        return rowNum >= targetNum;
//                    case "<=":
//                        return rowNum <= targetNum;
//                    default:
//                        System.out.println("Unsupported operator: " + operator);
//                        return false;
//                }
//            } else if (primitive.isString()) {
//                String rowStr = primitive.getAsString();
//
//                switch (operator) {
//                    case "=":
//                        return rowStr.equals(value);
//                    case "!=":
//                        return !rowStr.equals(value);
//                    default:
//                        System.out.println("Unsupported operator for strings: " + operator);
//                        return false;
//                }
//            }
//        }
//
//        return false; // 其他情况一律不匹配
//    }
}

