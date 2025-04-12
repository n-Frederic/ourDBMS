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

    public static void addColumn(String tableName, String columnName, Schema.ColumnRule newColumnRule) {
        Path schemaPath = From_schema(tableName);
        Path dataPath = From_data(tableName);

        JsonObject schema;
        try (FileReader reader = new FileReader(schemaPath.toFile())) {
            schema = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonArray fields = schema.getAsJsonArray("fields");

        // 检查是否已存在字段名
//        for (JsonElement f : fields) {
//            JsonObject field = f.getAsJsonObject();
//            if (field.get("fieldName").getAsString().equals(columnName)) {
//                System.out.println("Field already exists.");
//                return;
//            }
//        }

        JsonObject newField = new JsonObject();
        newField.addProperty("fieldName", columnName);
        JsonArray constraintsArray = new JsonArray();

        JsonObject typeObj = new JsonObject();
        typeObj.addProperty("Type", newColumnRule.getType());
        constraintsArray.add(typeObj);

        if (newColumnRule.isNotNull()) {
            JsonObject notNullObj = new JsonObject();
            notNullObj.addProperty("NOT NULL", true);
            constraintsArray.add(notNullObj);
        }

        if (newColumnRule.getDefaultValue() != null) {
            JsonObject defaultObj = new JsonObject();
            defaultObj.addProperty("Default", newColumnRule.getDefaultValue().toString());
            constraintsArray.add(defaultObj);
        }

        newField.add("constraint", constraintsArray);
        fields.add(newField);

        try (FileWriter writer = new FileWriter(schemaPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schema, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 同步更新 data 文件，为所有旧数据添加新字段默认值
        JsonArray data = readData(dataPath);
        for (JsonElement e : data) {
            JsonObject obj = e.getAsJsonObject();
            if (newColumnRule.getDefaultValue() != null) {
                obj.addProperty(columnName, newColumnRule.getDefaultValue().toString());
            } else {
                obj.add(columnName, JsonNull.INSTANCE);
            }
        }

        try (FileWriter writer = new FileWriter(dataPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteColumn(String tableName, String columnName) {
        // 处理 schema
        Path schemaPath = From_schema(tableName);
        JsonObject schemaObj;
        try (FileReader reader = new FileReader(schemaPath.toFile())) {
            schemaObj = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonArray fields = schemaObj.getAsJsonArray("fields");
        JsonArray newFields = new JsonArray();

        for (JsonElement elem : fields) {
            JsonObject field = elem.getAsJsonObject();
            if (!field.get("fieldName").getAsString().equals(columnName)) {
                newFields.add(field);
            }
        }

        schemaObj.add("fields", newFields);

        try (FileWriter writer = new FileWriter(schemaPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schemaObj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 处理 data
        Path dataPath = From_data(tableName);
        JsonArray data = readData(dataPath);

        for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();
            obj.remove(columnName);
        }

        try (FileWriter writer = new FileWriter(dataPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void renameColumn(String tableName, String oldName, String newName) {
        // 修改 schema
        Path schemaPath = From_schema(tableName);
        JsonObject schemaObj;
        try (FileReader reader = new FileReader(schemaPath.toFile())) {
            schemaObj = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonArray fields = schemaObj.getAsJsonArray("fields");
        for (JsonElement elem : fields) {
            JsonObject field = elem.getAsJsonObject();
            if (field.get("fieldName").getAsString().equals(oldName)) {
                field.addProperty("fieldName", newName);
                break;
            }
        }

        try (FileWriter writer = new FileWriter(schemaPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schemaObj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 修改 data
        Path dataPath = From_data(tableName);
        JsonArray data = readData(dataPath);

        for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has(oldName)) {
                JsonElement value = obj.remove(oldName);
                obj.add(newName, value);
            }
        }

        try (FileWriter writer = new FileWriter(dataPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
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

