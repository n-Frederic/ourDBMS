package Util.Func;

import Storage.Page.Tuple;


import java.util.*;
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

    public static void drawDatabaseList(List<String> dbNames) {

        // 1. 先把 List<String> 转成 JsonArray，其中每一行只有一列：Database
        ArrayList<Tuple> data = new ArrayList<>();
        for (String name : dbNames) {
            Tuple obj = new Tuple();
            // obj.addProperty("Database", name);
            data.add(obj);
        }

        // 2. 构造列名列表
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Database");

        // 3. 直接复用现有的 DrawSelectedTable
        DrawSelectedTable(data, columns);
    }
    public static void drawTablesList(List<String> tbNames) {

        // 1. 先把 List<String> 转成 JsonArray，其中每一行只有一列：Database
        ArrayList<Tuple> data = new ArrayList<>();
        for (String name : tbNames) {
            Tuple obj = new  Tuple();
            // obj.addProperty("Table", name);
            data.add(obj);
        }

        // 2. 构造列名列表
        ArrayList<String> columns = new ArrayList<>();
        columns.add("Table");

        // 3. 直接复用现有的 DrawSelectedTable
        DrawSelectedTable(data, columns);
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
     * @param tuples 行的数组数据。
     * @param columns 指定的列名列表。

     */
    public static void DrawSelectedTable(ArrayList<Tuple> tuples, ArrayList<String> columns) {

        DrawSelectedTable( tuples,columns);

        System.out.println(columns);

        Map<String, Integer> columnWidths=new HashMap<>();
        for (String column : columns) {
            columnWidths.put(column, column.length());
        }
        for (var tuple : tuples) {
            for (int i = 0; i < columns.size(); i++) {
                String val = tuple.getValue(i).toString();
                columnWidths.put(columns.get(i), Math.max(columnWidths.get(columns.get(i)), getDisplayWidth(val)));
            }
        }

/*        for (var rowElement : data) {
            for (int i = 0; i < tuples.size(); i++) {
                String val = row.has(col) ? row.get(col).getAsString() : "";
                columnWidths.put(col, Math.max(columnWidths.get(col), getDisplayWidth(val)));
            }
        }*/

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

        for (var tuple : tuples) {
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                String val = tuple.getValue(i).toString();
                rowMap.put(columns.get(i), val);
            }
            printRow.accept(rowMap);
        }
        printSeparator.run();

    }
}
