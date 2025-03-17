package moreight.Funtion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateTable {
    private static final String DIRECTORY = "../TestData";

    public static void CreateTable(String user, String database, String table) {
        try {
            final Path FILE_PATH = Paths.get(DIRECTORY,user, database, table+".json");
            if(!Files.exists(FILE_PATH)) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
