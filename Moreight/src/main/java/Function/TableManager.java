package Function;

import com.google.gson.JsonObject;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

public class TableManager {

    private static final String DIRECTORY = "../TestData";

    public static void CreateTable(String table, ArrayList<String[]> args) {
        try {
            final Path schemaPath = Paths.get(DIRECTORY,DatabaseManager.currentDatabase, table + "_schema.json");
            if(!Files.exists(schemaPath)) {
                JsonObject schemaJson = new JsonObject();
                for(String[] field : args) {
                    schemaJson.addProperty(field[0], field[1]);
                }
            } else System.out.println("The table has existed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DropTable(String table) {
        Path filePath = Paths.get(DIRECTORY + "/" + DatabaseManager.currentDatabase + "/" + table +".json");
        Path schemaPath = Paths.get(DIRECTORY + "/" + DatabaseManager.currentDatabase + "/" + table +"_schema.json");
        try {
            // 删除文件
            Files.delete(filePath);
            Files.delete(schemaPath);
            System.out.println("deleted successfully！");
        } catch (NoSuchFileException e) {
            System.err.println("not exist in: " + filePath);
        } catch (IOException e) {
            System.err.println("deleted unsuccessfully!" + e.getMessage());
        }
    }


}
