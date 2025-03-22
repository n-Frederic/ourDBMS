package Operate;

import Function.DatabaseManager;
import Parser.Field;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Table {
    private static final String DIRECTORY = "../TestData/DatabaseManager";

    public static void InsertIntoValue(String table, ArrayList<String> columns, ArrayList<Object> values) {
        try {
            Path dataPath = Paths.get(DIRECTORY, DatabaseManager.getCurrentDatabase(), table+"_data.json");
            JsonArray dataArray;

            if(Files.exists(dataPath)) {
                try (FileReader reader = new FileReader(dataPath.toFile())) {
                    Gson gson = new Gson();
                    dataArray = gson.fromJson(reader,JsonArray.class);
                }
            } else {
                dataArray = new JsonArray();
            }

            JsonObject newRow = new JsonObject();
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                Object value = values.get(i);

                switch (value) {
                    case String s -> newRow.addProperty(column, s);
                    case Integer integer -> newRow.addProperty(column, integer);
                    case Double v -> newRow.addProperty(column, v);
                    case Boolean b -> newRow.addProperty(column, b);
                    case null, default ->
                            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
                }
            }

            dataArray.add(newRow);

            try (FileWriter writer = new FileWriter(dataPath.toFile())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(dataArray, writer);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
