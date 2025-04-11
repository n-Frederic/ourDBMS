package Table;

import Conditions.Condition;
import Database.DatabaseManager;
import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Table {
    private static final String DIRECTORY = "../TestData/DatabaseManager";

    public static void Insert(Path dataPath, ArrayList<String> columns, ArrayList<Object> values) {
        JsonArray data;

        try (FileReader reader = new FileReader(dataPath.toFile())) {
            data = JsonParser.parseReader(reader).getAsJsonArray();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonObject newRow = new JsonObject();
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            Object value = values.get(i);

            if (value == null || value.toString().isEmpty()) {
                newRow.add(column, JsonNull.INSTANCE);
            } else {
                newRow.addProperty(column, value.toString());
            }
        }

        data.add(newRow);

        try (FileWriter writer = new FileWriter(dataPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addColumn(String dbName, String tableName, String columnName, Schema.ColumnRule newColumnRule) {
        // 获取数据路径和 schema 路径
        Path dataPath = From_data(tableName);
        Path schemaPath = From_schema(tableName);

        // 读取 schema 数据
        JsonArray schemaData = readSchema(schemaPath);
        // 读取数据表中的数据
        JsonArray data = readData(dataPath);

        // 检查 schema 中是否已经有该列
        for (JsonElement element : schemaData) {
            JsonObject field = element.getAsJsonObject();
            if (field.get("fieldName").getAsString().equals(columnName)) {
                System.out.println("Column already exists in schema.");
                return;
            }
        }

        // 创建新字段并添加到 schema 数据中
        JsonObject newField = new JsonObject();
        newField.addProperty("fieldName", columnName);

        JsonArray constraints = new JsonArray();
        JsonObject constraint = new JsonObject();
        constraint.addProperty("Type", newColumnRule.getType());
        constraint.addProperty("NOT NULL", newColumnRule.isNotNull());
        constraint.addProperty("Default", newColumnRule.getDefaultValue());
        constraints.add(constraint);

        newField.add("constraint", constraints);
        schemaData.add(newField);

        // 保存更新后的 schema
        try (FileWriter writer = new FileWriter(schemaPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schemaData, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 更新数据表中的每一条数据，加入新列
        for (JsonElement element : data) {
            JsonObject row = element.getAsJsonObject();
            row.addProperty(columnName, newColumnRule.getDefaultValue());  // 默认值
        }

        // 保存更新后的数据
        try (FileWriter writer = new FileWriter(dataPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Column added successfully.");
    }




    // set是针对于where筛选后的JsonArray修改列值
    public static void Set(JsonArray data, HashMap<String,String> map) {
        Iterator<JsonElement> iterator = data.iterator();

        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            JsonObject object = element.getAsJsonObject();

            for(String key : map.keySet()) {
                object.addProperty(key,map.get(key));
            }
        }
    }


    public static void Delete(JsonArray oldData, JsonArray newData) {
        Iterator<JsonElement> iterator = newData.iterator();

        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            if(oldData.contains(element)) oldData.remove(element);
        }
    }

    public static Path From_data(String table) {
        Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");
        return dataPath;
    }

    public static Path From_schema(String table) {
        Path schemaPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_schema.json");
        return schemaPath;
    }

    public static JsonArray Where(JsonArray a, JsonArray b, String mode) {
        JsonArray array = new JsonArray();
        if (mode.equals("and")) {
            for (JsonElement e : a) {
                if (b.contains(e)) {
                    array.add(e);
                }
            }
            return array;
        } else if (mode.equals("or")) {
            for (JsonElement e : a) {
                array.add(e);
            }
            for (JsonElement e : b) {
                if (!array.contains(e)) {
                    array.add(e);
                }
            }
            return array;
        } else return array;
    }

    public static void Where(JsonArray data, Condition condition) {
        Iterator<JsonElement> iterator = data.iterator();

        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            JsonObject object = element.getAsJsonObject();

            switch (condition.getOperator()) {
                case "=":
                    if (!object.get(condition.getColumn()).getAsString().equals(condition.getValue())) {
                        data.remove(object);
                    }
                    break;
                case "!=":
                    if (object.get(condition.getColumn()).getAsString().equals(condition.getValue())) {
                        data.remove(object);
                    }
                    break;
                case "<":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) >= 0) {
                        data.remove(object);
                    }
                    break;
                case ">":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) <= 0) {
                        data.remove(object);
                    }
                    break;
                case "<=":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) > 0) {
                        data.remove(object);
                    }
                    break;
                case ">=":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) < 0) {
                        data.remove(object);
                    }
                    break;
            }
        }

    }

    public static JsonArray readData(Path dataPath) {
        JsonArray data;
        try {
            FileReader reader = new FileReader(dataPath.toFile());
            data = JsonParser.parseReader(reader).getAsJsonArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public static JsonArray readSchema(Path schemaPath) {
        JsonArray fields;
        try {
            FileReader reader = new FileReader(schemaPath.toFile());
            fields = JsonParser.parseReader(reader).getAsJsonArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return fields;
    }

}

