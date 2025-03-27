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
import java.util.function.Consumer;

public class Table {
    private static final String DIRECTORY = "../TestData/DatabaseManager";



    public static void InsertIntoValue(String table, ArrayList<String> columns, ArrayList<Object> values) {
        try {
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table+"_data.json");
            JsonArray dataArray;

            if(Files.exists(dataPath)) {
                try (FileReader reader = new FileReader(dataPath.toFile())) {
                    Gson gson = new Gson();
                    dataArray = gson.fromJson(reader,JsonArray.class);
                }
            } else {
                dataArray = new JsonArray();
            }

            JsonObject newRow = new JsonObject();
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                Object value = values.get(i);

//                switch (value) {
//                    case String s -> newRow.addProperty(column, s);
//                    case Integer integer -> newRow.addProperty(column, integer);
//                    case Double v -> newRow.addProperty(column, v);
//                    case Boolean b -> newRow.addProperty(column, b);
//                    case null, default ->
//                            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
//                }
            }

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

    // 这个是SELECT * FROM table_name
    public static void SelectFromTable(String table) {
        Set<String> columnNames = new LinkedHashSet<>();
        Map<String, Integer> columnWidths = new LinkedHashMap<>();
        try {
            Path schemaPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table+"_schema.json");
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table+"_data.json");

            try (FileReader reader = new FileReader(dataPath.toFile())) {
                JsonArray data = JsonParser.parseReader(reader).getAsJsonArray();

                for(var rowElement : data) {
                    JsonObject row = rowElement.getAsJsonObject();
                    columnNames.addAll(row.keySet());
                }

                for(String column : columnNames) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void SelectFromTable(String table, ArrayList<String> columns, ArrayList<Object> values) {
//        try {
//            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");
//
//            try (FileReader reader = new FileReader(dataPath.toFile())) {
//                JsonArray data = JsonParser.parseReader(reader).getAsJsonArray();
//                Set<String> columnNames = new LinkedHashSet<>();
//                Map<String, Integer> columnWidths = new LinkedHashMap<>();
//
//                for (var rowElement : data) {
//                    JsonObject row = rowElement.getAsJsonObject();
//                    columnNames.addAll(row.keySet());
//                }
//
//                for (String column : columnNames) {
//                    columnWidths.put(column, column.length());
//                }
//
//                List<JsonObject> filteredData = new ArrayList<>();
//                for (var rowElement : data) {
//                    JsonObject row = rowElement.getAsJsonObject();
//                    boolean match = true;
//
//                    for (int i = 0; i < columns.size(); i++) {
//                        String col = columns.get(i);
//                        Object val = values.get(i);
//
//                        if (!row.has(col) || !row.get(col).getAsString().equals(val.toString())) {
//                            match = false;
//                            break;
//                        }
//                    }
//
//                    if (match) {
//                        filteredData.add(row);
//
//                        // 更新列宽
//                        for (String col : columnNames) {
//                            String val = row.has(col) ? row.get(col).getAsString() : "";
//                            columnWidths.put(col, Math.max(columnWidths.get(col), getDisplayWidth(val)));
//                        }
//                    }
//                }
//
//                if (filteredData.isEmpty()) {
//                    System.out.println("No records found matching the criteria.");
//                    return;
//                }
//
//                Runnable printSeparator = () -> {
//                    System.out.print("+");
//                    for (String col : columnNames) {
//                        int width = columnWidths.get(col);
//                        System.out.print("-".repeat(width + 2) + "+");
//                    }
//                    System.out.println();
//                };
//
//                Consumer<Map<String, String>> printRow = rowMap -> {
//                    System.out.print("|");
//                    for (String col : columnNames) {
//                        String val = rowMap.getOrDefault(col, "").replace("\t", "    ");
//                        int width = columnWidths.get(col);
//                        System.out.print(" " + padRight(val, width) + " |");
//                    }
//                    System.out.println();
//                };
//
//                printSeparator.run();
//                Map<String, String> headerMap = new LinkedHashMap<>();
//                for (String col : columnNames) headerMap.put(col, col);
//                printRow.accept(headerMap);
//                printSeparator.run();
//
//                for (JsonObject row : filteredData) {
//                    Map<String, String> rowMap = new LinkedHashMap<>();
//                    for (String col : columnNames) {
//                        String val = row.has(col) ? row.get(col).getAsString() : "";
//                        rowMap.put(col, val);
//                    }
//                    printRow.accept(rowMap);
//                }
//                printSeparator.run();
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//



    public static void SelectFromTable(String table, ArrayList<String> columns, ArrayList<Object> values, ArrayList<String> operators) {
        try {
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");

            try (FileReader reader = new FileReader(dataPath.toFile())) {
                JsonArray data = JsonParser.parseReader(reader).getAsJsonArray();
                Set<String> columnNames = new LinkedHashSet<>();
                Map<String, Integer> columnWidths = new LinkedHashMap<>();

                // 获取所有列名
                for (var rowElement : data) {
                    JsonObject row = rowElement.getAsJsonObject();
                    columnNames.addAll(row.keySet());
                }

                // 计算列宽
                for (String column : columnNames) {
                    columnWidths.put(column, column.length());
                }

                List<JsonObject> filteredData = new ArrayList<>();

                // 过滤数据
                for (var rowElement : data) {
                    JsonObject row = rowElement.getAsJsonObject();
                    boolean match = true;

                    for (int i = 0; i < columns.size(); i++) {
                        String col = columns.get(i);
                        Object val = values.get(i);
                        String operator = operators.get(i);

                        if (!row.has(col)) {
                            match = false;
                            break;
                        }

                        String rowVal = row.get(col).getAsString();

                        // 基于关系符号进行比较
                        switch (operator) {
                            case "=":
                                if (!rowVal.equals(val.toString())) {
                                    match = false;
                                }
                                break;
                            case ">":
                                if (Integer.parseInt(rowVal) <= (Integer) val) {
                                    match = false;
                                }
                                break;
                            case "<":
                                if (Integer.parseInt(rowVal) >= (Integer) val) {
                                    match = false;
                                }
                                break;
                            case ">=":
                                if (Integer.parseInt(rowVal) < (Integer) val) {
                                    match = false;
                                }
                                break;
                            case "<=":
                                if (Integer.parseInt(rowVal) > (Integer) val) {
                                    match = false;
                                }
                                break;
                            default:
                                match = false;
                        }

                        if (!match) {
                            break;
                        }
                    }

                    if (match) {
                        filteredData.add(row);

                        // 更新列宽
                        for (String col : columnNames) {
                            String val = row.has(col) ? row.get(col).getAsString() : "";
                            columnWidths.put(col, Math.max(columnWidths.get(col), getDisplayWidth(val)));
                        }
                    }
                }

                if (filteredData.isEmpty()) {
                    System.out.println("No records found matching the criteria.");
                    return;
                }

                // 打印表格
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

                for (JsonObject row : filteredData) {
                    Map<String, String> rowMap = new LinkedHashMap<>();
                    for (String col : columnNames) {
                        String val = row.has(col) ? row.get(col).getAsString() : "";
                        rowMap.put(col, val);
                    }
                    printRow.accept(rowMap);
                }
                printSeparator.run();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
