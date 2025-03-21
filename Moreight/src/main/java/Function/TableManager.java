package Function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

public class TableManager {

    private static final String DIRECTORY = "../TestData";

    public static void CreateTable(String table, ArrayList<String[]> args) {
        try {
            final Path schemaPath = Paths.get(DIRECTORY,DatabaseManager.getCurrentDatabase(), table + "_schema.json");
            if(!Files.exists(schemaPath)) {
                JsonObject schemaJson = new JsonObject();
                for(String[] field : args) {
                    schemaJson.addProperty(field[0], field[1]);
                }

                try (FileWriter writer = new FileWriter(schemaPath.toFile())) {
                    Gson gson = new Gson();
                    gson.toJson(schemaJson,writer);
                    writer.flush();
                }
            } else System.out.println("The table has existed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void DropTable(String table) {
        Path filePath = Paths.get(DIRECTORY,DatabaseManager.getCurrentDatabase(), table +".json");
        Path schemaPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table +"_schema.json");
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
