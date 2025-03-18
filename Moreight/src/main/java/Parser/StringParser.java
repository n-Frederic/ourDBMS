package Parser;

import Function.TableManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringParser {

    public static Map<String, Field> parseCreateTable(String fieldsStr) {
        String[] lines = fieldsStr.trim().split("\\s*,\\s*");
        Map<String, Field> fieldMap = new LinkedHashMap<>();
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

            Field field = new Field();
            field.setName(matcher.group(1));  // 字段名
            field.setType(matcher.group(2));  // 类型

            // 解析约束（组3）
            String constraints = matcher.group(3);
            if (constraints != null) {
                // 将约束转为大写，统一处理大小写
                String upperConstraints = constraints.toUpperCase();
                field.setPrimaryKey(upperConstraints.contains("PRIMARY KEY"));
                field.setUnique(upperConstraints.contains("UNIQUE"));
                field.setNotNUll(upperConstraints.contains("NOT NULL"));
            }
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }
}
