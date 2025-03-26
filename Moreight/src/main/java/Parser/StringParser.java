package Parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public static List<Map<String, String>> parseWhere(String str){
        List<Map<String, String>> filtList = new LinkedList<>();
        //解析字段为3组
        Pattern fieldPattern = Pattern.compile(
                "(\\w+)\\s+" +
                        "([^\\s]+(?:\\([^)]+\\))?)" +
                        "(?:\\s+(.*))?"
        );//分123组
        Matcher singleMatcher = fieldPattern.matcher(str + ";");
        while (singleMatcher.find()) {
            Map<String, String> filtMap = new LinkedHashMap<>();

            filtMap.put("fieldName", singleMatcher.group(1));
            filtMap.put("relationshipName", singleMatcher.group(2));
            filtMap.put("condition", singleMatcher.group(3));

            filtList.add(filtMap);
        }
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
