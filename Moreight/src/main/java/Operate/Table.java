package Operate;

import Function.DatabaseManager;
import Parser.Field;
import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Table {
    private static final String DIRECTORY = "../TestData/DatabaseManager";



    public static void InsertIntoValue(String table, ArrayList<String> columns, ArrayList<Object> values) {
        try {
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");
            JsonArray dataArray;

            if (Files.exists(dataPath)) {
                try (FileReader reader = new FileReader(dataPath.toFile())) {
                    Gson gson = new Gson();
                    dataArray = gson.fromJson(reader, JsonArray.class);
                }
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

    public static void deleteValue(String table, ArrayList<Condition>conditions){

    }



    // 这个是SELECT * FROM table_name
    public static void SelectFromTable(String table) {
        Set<String> columnNames = new LinkedHashSet<>();
        Map<String, Integer> columnWidths = new LinkedHashMap<>();
        try {
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");

            try (FileReader reader = new FileReader(dataPath.toFile())) {
                JsonArray data = JsonParser.parseReader(reader).getAsJsonArray();
                DrawSelectedTable(data, columnNames, columnWidths);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SelectFromTable(String table, ArrayList<String> column,ArrayList<Condition> conditions) {
        Set<String> columnNames = new LinkedHashSet<>();
        Map<String, Integer> columnWidths = new LinkedHashMap<>();
        try {
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");

            try (FileReader reader = new FileReader(dataPath.toFile())) {
                JsonArray data = JsonParser.parseReader(reader).getAsJsonArray();
                ArrayList<JsonElement> arrayList = JsonArrayToArrayList(data);
                for(Condition condition : conditions) {
                    for(JsonElement element : arrayList) {
                        JsonObject object = element.getAsJsonObject();
                        switch (condition.getOperator()) {
                            case "=" :
                                if(object.get(condition.getColumn()) != condition.getValue()) {
                                    if(arrayList.contains(element))
                                        arrayList.remove(element);
                                }
                                break;
                            case "!=":
                                if(object.get(condition.getColumn()) == condition.getValue()) {
                                    if(arrayList.contains(element))
                                        arrayList.remove(element);
                                }
                            case "<=":


                        }
                    }
                }


                DrawSelectedTable(data, columnNames, columnWidths);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static void DrawSelectedTable(JsonArray data, Set<String> columnNames, Map<String, Integer> columnWidths) {
        for (var rowElement : data) {
            JsonObject row = rowElement.getAsJsonObject();
            columnNames.addAll(row.keySet());
        }

        for (String column : columnNames) {
            columnWidths.put(column, column.length());
        }
        for (var rowElement : data) {
            JsonObject row = rowElement.getAsJsonObject();
            for (String col : columnNames) {
                String val = row.has(col) ? row.get(col).getAsString() : "";
                columnWidths.put(col, Math.max(columnWidths.get(col), getDisplayWidth(val)));
            }
        }

        Runnable printSeparator = () -> {
            System.out.print("+");
            for (String col : columnNames) {
                int width = columnWidths.get(col);
                System.out.print("-".repeat(width + 2) + "+");
            }
            System.out.println();
        };

        Consumer<Map<String, String>> printRow = rowMap -> {
            System.out.print("|");
            for (String col : columnNames) {
                String val = rowMap.getOrDefault(col, "").replace("\t", "    ");
                int width = columnWidths.get(col);
                System.out.print(" " + padRight(val, width) + " |");
            }
            System.out.println();
        };

        printSeparator.run();
        Map<String, String> headerMap = new LinkedHashMap<>();
        for (String col : columnNames) headerMap.put(col, col);
        printRow.accept(headerMap);
        printSeparator.run();

        for (var rowElement : data) {
            JsonObject row = rowElement.getAsJsonObject();
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (String col : columnNames) {
                String val = row.has(col) ? row.get(col).getAsString() : "";
                rowMap.put(col, val);
            }
            printRow.accept(rowMap);
        }
        printSeparator.run();

    }

    private static ArrayList<JsonElement> JsonArrayToArrayList(JsonArray jsonArray) {
        ArrayList<JsonElement> array = new ArrayList<>();
        for(JsonElement element : jsonArray) {
            array.add(element);
        }
        return array;
    }

    private static JsonArray ArrayListToJsonArray(ArrayList<JsonElement> arrayList) {
        JsonArray array = new JsonArray();
        for(JsonElement element : arrayList) {
            array.add(element);
        }
        return array;
    }

    public static JsonArray selectColumns(JsonArray originalArray, ArrayList<String> columns) {
        JsonArray resultArray = new JsonArray();

        for (int i = 0; i < originalArray.size(); i++) {
            JsonObject originalObj = originalArray.get(i).getAsJsonObject();
            JsonObject filteredObj = new JsonObject();

            for (String column : columns) {
                if (originalObj.has(column)) {
                    filteredObj.add(column, originalObj.get(column));
                }
            }

            resultArray.add(filteredObj);
        }

        return resultArray;
    }
}