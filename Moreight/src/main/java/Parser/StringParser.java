package Parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringParser {

    public static Map<String, Field> parseCreateTable(String fieldsStr) {
        String[] lines = fieldsStr.trim().split("\\s*,\\s*");//分隔字符串去除首尾空格
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        //解析字段为3组
        Pattern fieldPattern = Pattern.compile(
                "(\\w+)\\s+" +
                        "([^\\s]+(?:\\([^)]+\\))?)" +
                        "(?:\\s+(.*))?"
        );//分123组
        for(String line:lines){
            line=line.trim();

            Matcher matcher = fieldPattern.matcher(line);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid field definition: " + line);
            }

            Field field = new Field(matcher.group(1), matcher.group(2));
//            field.setName(matcher.group(1));  // 字段名
//            field.setType(matcher.group(2));  // 类型

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
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }



    public static Map<String,Field> parseUpdateSet(String fieldsStr){
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        return fieldMap;
    }

    public static List<Map<String,String>> parseWhere_join(String str, Map<String, Map<String, Field>> fieldMaps){
        List<Map<String, String>> joinConditionList = new LinkedList<>();
        return joinConditionList;
    }

    public static List<Map<String, String>> parseWhere(String str, String tableName, Map<String, Field> fieldMap){
        List<Map<String, String>> filtList = new LinkedList<>();
        return filtList;
    }

    public static List<Map<String, String>> parseWhere(String str){
        List<Map<String, String>> filtList = new LinkedList<>();
        return filtList;
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
