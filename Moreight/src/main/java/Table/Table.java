package Table;

import Conditions.Condition;
import Conditions.ConditionNode;
import Database.DatabaseManager;
import Storage.BPlusTree.BPlusNode;
import Storage.BPlusTree.BPlusTree;
import Storage.BPlusTree.LeafNode;
import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Table类用于管理数据库表的操作。
 * 它支持插入数据、添加列、更新数据、删除数据等功能，表的结构定义和数据分别存储在JSON文件中。
 */
public class Table {
    Schema schema ;
    BPlusTree tree;
    private static final String DIRECTORY = "../TestData/DatabaseManager";

    public Table() {

    }


    /**
     * 向表中插入一条新记录。
     * @param dataPath 表的数据文件路径。
     * @param columns 要插入的列名列表。
     * @param values 要插入的值列表。
     */
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

    public void Insert(ArrayList<String> columns, ArrayList<Object> values) {
        LeafNode root = tree.getRoot();
        if(columns.contains(schema.getPrimaryKeyName()))

    }

    public static void addColumn(String tableName, Field newField) {
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

        // 添加新字段结构
        JsonObject newFieldJson = new JsonObject();
        newFieldJson.addProperty("fieldName", newField.getName());

        JsonArray constraintsArray = new JsonArray();

        JsonObject typeObj = new JsonObject();
        typeObj.addProperty("Type", newField.getType());
        constraintsArray.add(typeObj);
        System.out.println(newFieldJson);
        System.out.println(typeObj);

        if (newField.isNotNull()) {
            JsonObject notNullObj = new JsonObject();
            notNullObj.addProperty("NOT NULL", true);
            constraintsArray.add(notNullObj);
        }

        if (newField.isUnique()) {
            JsonObject uniqueObj = new JsonObject();
            uniqueObj.addProperty("UNIQUE", true);
            constraintsArray.add(uniqueObj);
        }

        if (newField.isPrimaryKey()) {
            JsonObject pkObj = new JsonObject();
            pkObj.addProperty("PRIMARY KEY", true);
            constraintsArray.add(pkObj);
        }

        if (newField.getDefault() != null && !newField.getDefault().isEmpty()) {
            JsonObject defaultObj = new JsonObject();
            defaultObj.addProperty("Default", newField.getDefault());
            constraintsArray.add(defaultObj);
        }

        newFieldJson.add("constraint", constraintsArray);
        fields.add(newFieldJson);

        try (FileWriter writer = new FileWriter(schemaPath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schema, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 更新所有旧数据记录，添加新字段默认值
        JsonArray data = readData(dataPath);
        for (JsonElement e : data) {
            JsonObject obj = e.getAsJsonObject();
            if (newField.getDefault() != null && !newField.getDefault().isEmpty()) {
                obj.addProperty(newField.getName(), newField.getDefault());
            } else {
                obj.add(newField.getName(), JsonNull.INSTANCE);
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

    // 更新内存并保存到磁盘


    public static void saveData(String tableName, JsonArray data) {
        Path dataPath = From_data(tableName);
        try (FileWriter writer = new FileWriter(dataPath.toFile())) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("写入表 " + tableName + " 失败", e);
        }
    }
    public static void Set(String tableName, HashMap<String,String> map, ConditionNode logicTree) {
        // 1. 读出整表
        JsonArray all = readData(From_data(tableName));

        // 2. 逻辑过滤，返回的是 all 中的部分元素（原始引用）
        JsonArray data = logicTree.evaluate(all);

        // 3. 就地修改这些元素
        for (JsonElement e : data) {
            JsonObject obj = e.getAsJsonObject();
            for (Map.Entry<String,String> entry : map.entrySet()) {
                obj.addProperty(entry.getKey(), entry.getValue());
            }
        }

        // 4. 最后将 all 整个写回文件
        saveData(tableName, all);
    }

    // set是针对于where筛选后的JsonArray修改列值
//    public static void Set(String tableName, HashMap<String,String> map, ConditionNode logicTree) {
////
//        JsonArray all = Table.From_data(tableName);
//
//// 逻辑过滤，返回的是 all 中的部分元素，但它们和 all 中是同一个 JsonObject 引用
//        JsonArray data = logicTree.evaluate();
//
//// 就地修改
//        for (JsonElement e : data) {
//            JsonObject obj = e.getAsJsonObject();
//            for (Map.Entry<String,String> entry : map.entrySet()) {
//                obj.addProperty(entry.getKey(), entry.getValue());
//            }
//        }
//
//// all 已经被改了，后面只要把 all 序列化/写文件就行
//
//    }


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
                        iterator.remove(); // 使用 iterator.remove() 删除当前元素
                    }
                    break;
                case "!=":
                    if (object.get(condition.getColumn()).getAsString().equals(condition.getValue())) {
                        iterator.remove(); // 使用 iterator.remove() 删除当前元素
                    }
                    break;
                case "<":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) >= 0) {
                        iterator.remove(); // 使用 iterator.remove() 删除当前元素
                    }
                    break;
                case ">":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) <= 0) {
                        iterator.remove(); // 使用 iterator.remove() 删除当前元素
                    }
                    break;
                case "<=":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) > 0) {
                        iterator.remove(); // 使用 iterator.remove() 删除当前元素
                    }
                    break;
                case ">=":
                    if (object.get(condition.getColumn()).getAsString().compareTo(condition.getValue()) < 0) {
                        iterator.remove(); // 使用 iterator.remove() 删除当前元素
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


