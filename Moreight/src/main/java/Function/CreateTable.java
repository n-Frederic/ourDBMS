package Function;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateTable {
    private static final String DIRECTORY = "../TestData";

    public static void CreateTable(String user, String database, String table, ArrayList<String[]> args) {
        try {
            final Path schemaPath = Paths.get(DIRECTORY,user, database, table + "_schema.json");
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
}
