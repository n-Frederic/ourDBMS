package Parser;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Operate.Condition;




public class StringParser {

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
            line=line.trim();

            Matcher matcher = fieldPattern.matcher(line);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid field definition: " + line);
            }

            Field field = new Field(matcher.group(1), matcher.group(2));
//            field.setName(matcher.group(1));  // 字段名
//            field.setType(matcher.group(2));  // 类型
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


    public static ArrayList<String> parseInsertColumn(String columnsStr) {
        // 正则表达式，用来匹配 SQL 语句中的表名、列名和对应的值


            // 处理列名和对应的值
            ArrayList<String> columns = new ArrayList<>();

            // 列名处理
            String[] columnsArray = columnsStr.split("\\s*,\\s*");
            for (String column : columnsArray) {
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

    public static List<Map<String,String>> parseWhere_join(String str, Map<String, Map<String, Field>> fieldMaps){
        List<Map<String, String>> joinConditionList = new LinkedList<>();
        return joinConditionList;
    }


    /**
     * SQL WHERE 多表子句字符串
     *"users.id = 1 AND orders.user_id = 1 AND users.age > 20"
     *@return 一个包含多个 Map 的列表，每个 Map 表示一个条件，包含三个键值对：
     *       - "fieldName": 列名，例如 "column1"
     *       - "relationshipName": 关系运算符，例如 "="、">" 等
     *       - "condition": 数值条件，例如 "value1"
     */
    public static List<Map<String, String>> parseWhere(String str, String tableName, Map<String, Field> fieldMap){
        List<Map<String, String>> filtList = new LinkedList<>();
        if (null == str) {
            return filtList;
        }
        Pattern fieldPattern = Pattern.compile(
                "(\\w+)\\s+" +
                        "([^\\s]+(?:\\([^)]+\\))?)" +
                        "(?:\\s+(.*))?"
        );//分123组
        Matcher singleMatcher = fieldPattern.matcher(str);
        while (singleMatcher.find()) {
            String fieldName = singleMatcher.group(1);
            //如果包含table.id这样的型式，将table名进行匹配，如果不匹配则跳过
            if (fieldName.contains(".")) {
                String[] field = fieldName.split("\\.");
                //如果不匹配就跳过
                if (!tableName.equals(field[0])) {
                    continue;
                } else {
                    //匹配
                    fieldName = field[1];
                }
            }
            Field field = fieldMap.get(fieldName);
            if (null != field) {
                Map<String, String> filtMap = new LinkedHashMap<>();
                filtMap.put("fieldName", fieldName);
                filtMap.put("relationshipName", singleMatcher.group(2));
                filtMap.put("condition", singleMatcher.group(3));

                filtList.add(filtMap);
            }

        }
        return filtList;
    }

    /**
     * SQL WHERE 子句字符串
     * "age > 20 AND salary = 5000"
     * @return 一个包含多个 Map 的列表，每个 Map 表示一个条件，包含三个键值对：
     *          - "fieldName": 列名，例如 "column1"
     *          - "relationshipName": 关系运算符，例如 "="、">" 等
     *          - "condition": 数值条件，例如 "value1"
     */
    public static ArrayList<Condition> parseWhere(String str){
        ArrayList<Condition> filtList = new ArrayList<>();
        //解析字段为3组
        Pattern fieldPattern = Pattern.compile(
                "(\\w+)\\s+" +
                        "([^\\s]+(?:\\([^)]+\\))?)" +
                        "(?:\\s+(.*))?"
        );//分123组
        Matcher singleMatcher = fieldPattern.matcher(str + ";");
        while (singleMatcher.find()) {
            Condition filtMap;
            filtMap = new Condition(singleMatcher.group(1),singleMatcher.group(2),singleMatcher.group(3));
        }
        return filtList;
    }

    public static ArrayList<String> parseSelectColumn(String str){
        ArrayList<String> Coulumns=new ArrayList<>();
        return Coulumns;
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
