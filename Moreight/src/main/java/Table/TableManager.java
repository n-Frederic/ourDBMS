package Table;

import Database.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.io.IOException;

/**
 * TableManager类用于管理数据库中的表。
 * 它支持表的创建和删除功能，表的结构定义和数据分别存储在JSON文件中。
 */
public class TableManager {

    private static final String DIRECTORY = "../TestData/DatabaseManager";

    /**
     * 创建新表。
     * @param table 表名。
     * @param args 包含表字段定义的Field对象列表。
     */
    // (1) type (2) PRIMARY KEY (3) UNIQUE (4) NOT NULL (5) DEFAULT
    public static void CreateTable(String table, ArrayList<Field> args) {
        try {
            final Path schemaPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_schema.json");
            final Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_data.json");
            if (!Files.exists(schemaPath)) {
                JsonObject schemaJson = new JsonObject();
                JsonArray fieldsArray = new JsonArray();
                for (Field field : args) {
                    JsonObject fieldJson = new JsonObject();
                    fieldJson.addProperty("fieldName", field.getName());
                    JsonArray constraints = getConstraints(field);
                    fieldJson.add("constraint", constraints);
                    fieldsArray.add(fieldJson);
                }

                schemaJson.addProperty("table",table);
                schemaJson.add("fields",fieldsArray);

                try (FileWriter writer = new FileWriter(schemaPath.toFile())) {
                    Gson gson = new Gson();
//                    System.out.println(schemaJson);
                    gson.toJson(schemaJson, writer);
                    writer.flush();
                }

                if (!Files.exists(dataPath)) {
                    JsonArray emptyData = new JsonArray();
                    try (FileWriter writer = new FileWriter(dataPath.toFile())) {
                        Gson gson = new Gson();
                        gson.toJson(emptyData, writer);
                        writer.flush();
                    }
                }

            } else System.out.println("The table has existed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将Field对象的约束条件转换为JSON数组。
     * @param field 字段定义对象。
     * @return 包含约束条件的JSON数组。
     */
    private static JsonArray getConstraints(Field field) {
        JsonObject type = new JsonObject();
        JsonObject primaryKey = new JsonObject();
        JsonObject unique = new JsonObject();
        JsonObject notNull = new JsonObject();
        JsonObject Default = new JsonObject();
        type.addProperty("Type", field.getType());
        primaryKey.addProperty("PRIMARY KEY", field.isPrimaryKey());
        unique.addProperty("UNIQUE", field.isUnique());
        notNull.addProperty("NOT NULL", field.isNotNUll());
        Default.addProperty("Default", field.getDefault());
        JsonArray constraints = new JsonArray();
        constraints.add(type);
        constraints.add(primaryKey);
        constraints.add(unique);
        constraints.add(notNull);
        constraints.add(Default);
        return constraints;
    }

    /**
     * 删除表。
     * @param table 表名。
     * @param userLevel 用户权限等级（1为游客，其他为管理员）。
     */
    public static void DropTable(String table, int userLevel) {
        if (userLevel == 1) {
            return;
        }
        Path filePath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + ".json");
        Path schemaPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table + "_schema.json");
        try {
            // 删除文件
            Files.delete(schemaPath);
            System.out.println("deleted successfully : " + schemaPath);
        } catch (NoSuchFileException e) {
            System.err.println("not exist in: " + schemaPath);
        } catch (IOException e) {
            System.err.println("deleted unsuccessfully!" + e.getMessage());
        }

        try {
            Files.delete(filePath);
            System.out.println("deleted successfully : " + filePath);
        } catch (NoSuchFileException e) {
            System.err.println("not exist in: " + filePath);
        } catch (IOException e) {
            System.err.println("deleted unsuccessfully!" + e.getMessage());
        }
    }


}
