package Parser;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Conditions.Condition;
import Table.Field;

public class commandParser {

    static Set<String> validTypes = Set.of("int", "string", "float", "boolean");

    public static ArrayList<Field> parseCreateTable(String fieldsStr) {//！！！！
        String[] lines = fieldsStr.trim().split("\\s*,\\s*");//分隔字符串去除首尾空格
        ArrayList<Field>fieldList = new ArrayList<>();
        //解析字段为3组
        Pattern fieldPattern = Pattern.compile(
                "(\\w+)\\s+" +
                        "([^\\s]+(?:\\([^)]+\\))?)" +
                        "(?:\\s+(.*))?"
        );//分123组
        for(String line:lines){
            line=line.trim();//去除空格影响

            Matcher matcher = fieldPattern.matcher(line);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid field definition: " + line);
            }

            Field field = new Field(matcher.group(1), matcher.group(2));
            if (!validTypes.contains(field.getType())){
                System.out.println("类型不合法");
                return null;
            }

            // 解析约束（组3）
            String constraints = matcher.group(3);
            if (constraints != null) {
                String upperConstraints = constraints.toUpperCase();
                field.setPrimaryKey(upperConstraints.contains("PRIMARY KEY"));
                field.setUnique(upperConstraints.contains("UNIQUE") || field.isPrimaryKey());
                field.setNotnull(upperConstraints.contains("NOT NULL") || field.isPrimaryKey());
                if (upperConstraints.contains("DEFAULT")) {
                    int index = upperConstraints.indexOf("DEFAULT");
                    String defaultValue = constraints.substring(index + "DEFAULT".length()).trim();
                    field.setDefault(defaultValue);
                }
            }
            fieldList.add(field);
        }
        return fieldList;
    }

    public static Map<String,String> parseUpdateSet(String Str){
        String[] line=Str.trim().split("\\s*,\\s*");
        Map<String, String> fieldMap = new LinkedHashMap<>();

        //解析字段为3组
        Pattern fieldPattern = Pattern.compile(
                "(\\w+)\\s+" +
                        "([^\\s]+(?:\\([^)]+\\))?)" +
                        "(?:\\s+(.*))?"
        );//分123组

        for (String setStr : line) {
            //修改了正则规则，需要末尾加;或空格才能匹配
            Matcher relMatcher = fieldPattern.matcher(setStr + ";");
            relMatcher.find();
            //将组1做为key，组3作为value
            fieldMap.put(relMatcher.group(1), relMatcher.group(3));
        }

        return fieldMap;
    }

    public static ArrayList<String> parseAlter(String str){
        ArrayList<String> Alteralter=new ArrayList<>();
        


        return Alteralter;
    }

    public static ArrayList<String> parseAlteralter(String str){
        ArrayList<String> Columns=new ArrayList<>();
        return Columns;
    }

    public static ArrayList<String> parseAlterDrop(String str){
        ArrayList<String> Columns=new ArrayList<>();
        if (str == null) {
            return Columns;
        }
        String word=str.trim().toLowerCase();
        String[] lines=word.split(",");
        int i=1;
        for(String line:lines){
            line=line.trim();
            if(line.contains("count")||line.contains("sum")||line.contains("avg")||line.contains("min")||line.contains("max")){
                int index=line.indexOf("as");
                if(index!=-1){
                    Columns.add(line.substring(index+"as".length()).trim());
                }else{
                    Columns.add("Column"+i);
                    i++;
                }
            }else{
                Columns.add(line);
            }
        }
        return Columns;
    }


    public static ArrayList<Field> parseAlterAdd(String str){
        String[] lines =str.trim().split("\\s*,\\s*");//分隔字符串去除首尾空格
        ArrayList<Field>fieldList = new ArrayList<>();
        //解析字段为3组
        Pattern fieldPattern = Pattern.compile(
                "(\\w+)\\s+" +
                        "([^\\s]+(?:\\([^)]+\\))?)" +
                        "(?:\\s+(.*))?"
        );//分123组
        for(String line:lines){
            line=line.trim();//去除空格影响

            Matcher matcher = fieldPattern.matcher(line);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid field definition: " + line);
            }

            Field field = new Field(matcher.group(1), matcher.group(2));
            if (!validTypes.contains(field.getType())){
                System.out.println("类型不合法");
                return null;
            }

            // 解析约束（组3）
            String constraints = matcher.group(3);
            if (constraints != null) {
                String upperConstraints = constraints.toUpperCase();
                field.setPrimaryKey(upperConstraints.contains("PRIMARY KEY"));
                field.setUnique(upperConstraints.contains("UNIQUE") || field.isPrimaryKey());
                field.setNotnull(upperConstraints.contains("NOT NULL") || field.isPrimaryKey());
                if (upperConstraints.contains("DEFAULT")) {
                    int index = upperConstraints.indexOf("DEFAULT");
                    String defaultValue = constraints.substring(index + "DEFAULT".length()).trim();
                    field.setDefault(defaultValue);
                }
            }
            fieldList.add(field);
        }
        return fieldList;
    }


    public static ArrayList<String> parseInsertColumn(String columnsStr) {
        // 正则表达式，用来匹配 SQL 语句中的表名、列名和对应的值
        // 检查输入是否为 null 或空字符串
        if (columnsStr == null || columnsStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<String> columns = new ArrayList<>();
        // 列名处理
        String[] columnsArray = columnsStr.trim().split("\\s*,\\s*");
        for (String column : columnsArray) {
            column=column.trim();
            columns.add(column.trim());
        }
        return columns;
    }


    public static ArrayList<Object> parseInsertValue(String valuesStr ) {
        // 正则表达式，用来匹配 SQL 语句中的表名、列名和对应的值
            ArrayList<Object> values = new ArrayList<>();
            // 列名处理

            // 值处理
            String[] valuesArray = valuesStr.split("\\s*,\\s*");
            for (String value : valuesArray) {
                // 判断值的类型
                // 判断值的类型
                if (value.matches("'[^']+'") || value.matches("\"[^\"]+\"")) { // 字符串类型（用单引号或双引号括起来）
                    // 去除引号
                    values.add(value.substring(1, value.length() - 1));
                } else if (value.matches("-?\\d+")) { // 整数
                    values.add(Integer.parseInt(value));
                } else if (value.matches("-?\\d*\\.\\d+")) { // 浮动数字
                    values.add(Double.parseDouble(value));
                } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) { // 布尔值
                    values.add(Boolean.parseBoolean(value));
                } else {
                    // 默认情况下，如果值是未知类型，可以抛出异常或处理
                    throw new IllegalArgumentException("Unsupported value type: " + value);
                }
            }
           return values;
}
    public static String parseBetweenAnd(String string){
        if (string == null || string.isEmpty()) {
            return string;
        }

        // 定义正则表达式匹配 `BETWEEN AND` 条件
        String regex = "(.*?)\\s+(\\w+)\\s+between\\s+(\\S+)\\s+and\\s+(\\S+)(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher;

        // 使用循环处理多个 `BETWEEN AND` 条件
        while (true) {
            matcher = pattern.matcher(string);
            if (!matcher.matches()) {
                break; // 没有匹配到 `BETWEEN AND`，退出循环
            }

            // 提取匹配的组
            String prefix = matcher.group(1);
            String column = matcher.group(2);
            String lowerBound = matcher.group(3);
            String upperBound = matcher.group(4);
            String suffix = matcher.group(5);

            // 构造新的条件字符串
            String newCondition = prefix + " " + column + ">=" + lowerBound + " and " + column + "<=" + upperBound + " " + suffix;
            string = newCondition.trim();
        }

        return string; // 没有匹配到 `BETWEEN AND`，返回原始字符
    }

    /**
     * 解析 SQL WHERE 子句中的条件字符串，将其转换为 Condition 对象列表。
     *
     * @param str 输入的 SQL WHERE 子句中的条件字符串，例如 "name = 'Alice' AND age > 20"。
     * @return 返回一个 ArrayList<Condition>，其中每个 Condition 对象代表一个条件。
     *
     * 输入输出示例：
     * 输入: "name = 'Alice' AND age > 20"
     * 输出: [Condition{column='name', value=Alice, operator='='}, Condition{column=age, value=20, operator=>}]
     * 输入: "score >= 80 OR status = 'active'"
     * 输出: [Condition{column='score', value=80, operator='>='}, Condition{column=status, value=active, operator='='}]
     */
//    public static ArrayList<Condition> parseWhere(String str){
//        ArrayList<Condition> filtList = new ArrayList<>();
//        //解析字段为3组
//        Pattern fieldPattern = Pattern.compile(
//                        "(\\w+)\\s*" +                     // 列名（允许尾随空格）
//                        "(=|!=|>=|<=|>|<|LIKE|IN)\\s*" +       // 操作符（明确枚举支持的符号）
//                        "(.*)"                             // 值（剩余所有内容）
//        );
//        String string=str.trim();
//        String[] lines=string.split("(?i)\\s*(and|or)\\s*");
//        for (String line : lines) {
//            line=line.trim();
//            Matcher singleMatcher = fieldPattern.matcher(line);
//
//            if (singleMatcher.matches()) { // 确保匹配成功
//                String value=singleMatcher.group(3);
//                if(value.startsWith("'")&&value.endsWith("'")){
//                    value=value.substring(1,value.length()-1);
//                }else if (value.startsWith("\"") && value.endsWith("\"")){
//                    value=value.substring(1,value.length()-1);
//                }
//
//                Condition condition = new Condition(
//                        singleMatcher.group(1), // 列名
//                        value, // 值
//                        singleMatcher.group(2)  // 操作符
//                );
//                filtList.add(condition);
//            } else {
//                System.out.println("No match found for: " + line);
//            }
//        }
//        return filtList;
//    }
//

    /**
     * 解析SQL SELECT语句中的列部分，处理聚合函数并提取别名或生成默认列名
     *
     * @param str SQL SELECT语句的列部分字符串（例如："count(*) as total, sum(price)"）
     * @return ArrayList<String> 解析后的列名列表：
     *         - 包含聚合函数的列：提取AS别名或生成默认列名（Column0, Column1...）
     *         - 普通列：直接保留原始列名
     *
     * @example
     * 输入："count(*) as total, sum(price)"
     * 输出：total,Column1
     * 输入："name, age"
     * 输出：name,age
     */
    public static ArrayList<String> parseSelectColumn(String str){
        ArrayList<String> Columns=new ArrayList<>();
        if (str == null) {
            return Columns;
        }
        String word=str.trim().toLowerCase();
        String[] lines=word.split(",");
        int i=1;
        for(String line:lines){
            line=line.trim();
            if(line.contains("count")||line.contains("sum")||line.contains("avg")||line.contains("min")||line.contains("max")){
                int index=line.indexOf("as");
                if(index!=-1){
                    Columns.add(line.substring(index+"as".length()).trim());
                }else{
                    Columns.add("Column"+i);
                    i++;
                }
            }else{
                Columns.add(line);
            }
        }
        return Columns;
    }

    public static List<String> parseFrom(String str){
        String[] tableNames = str.trim().split(",");
        List<String> tableNameList = new ArrayList<>();
        for (String tableName : tableNames) {
            tableNameList.add(tableName.trim());
        }
        return tableNameList;
    }

    public static List<String> parseProjection(String str, String tableName, Map<String, Field> fieldMap){
        List<String> projectionList = new LinkedList<>();
        return projectionList;
    }



}
