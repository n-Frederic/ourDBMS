package Util.Func;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import Table.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Render类用于将JSON数据以表格形式输出到控制台。
 * 它支持字符宽度计算、字符串对齐和表格绘制。
 */
public class Render {

    /**
     * 判断字符是否为全角字符。
     * @param c 要判断的字符。
     * @return 如果是全角字符，返回true；否则返回false。
     */
    private static boolean isWideChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 计算字符串的显示宽度。
     * @param str 要计算的字符串。
     * @return 字符串的显示宽度。
     */
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

    /**
     * 将字符串右对齐并填充空格，使其符合指定的显示宽度。
     * @param str 要对齐的字符串。
     * @param displayWidth 指定的显示宽度。
     * @return 对齐后的字符串。
     */
    private static String padRight(String str, int displayWidth) {
        int currentWidth = getDisplayWidth(str);
        if (currentWidth >= displayWidth) {
            return str;
        }

        int spaceCount = displayWidth - currentWidth;
        return str + " ".repeat(spaceCount);
    }

    /**
     * 将JSON数组数据按照指定的列和列宽绘制为表格。
     * @param data JSON数组数据。
     * @param columns 指定的列名列表。

     */
    public static void DrawSelectedTable(JsonArray data, ArrayList<String> columns) {



        System.out.println(columns);

        Map<String, Integer> columnWidths=new HashMap<>();
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
