package Util.Func;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Render {

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
}
