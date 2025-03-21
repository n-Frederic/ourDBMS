package Function;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static String currentDatabase;

    public static String getCurrentDatabase(){
        return currentDatabase;
    }

    public static void createDataBase(String dbName) {
        currentDatabase = dbName;
        File folder =  new File("../TestData/" + dbName);

        if(!folder.exists()) {
            if(folder.mkdirs()) {
                System.out.println("CreateDatabase Successfully : " + folder.getAbsolutePath());
            } else {
                System.out.println("CreateDatabase failed.");
            }
        } else {
            System.out.println("The database has already existed : " + folder.getAbsolutePath());
        }
    }

    public static void useDatabase(String dbName){
        currentDatabase = dbName;
    }

    // 未测试版
    public static List<String> showDatabases(String dbName){
        File folder =  new File("../TestData/");
        List<String> databases = new ArrayList<>();

        if(!folder.exists()||!folder.isDirectory()){
            System.out.println("not exist.");
        }
        File[] files = folder.listFiles();
        if(files!=null){
            for(File file:files){
                if(file.isDirectory()){
                    databases.add(file.getName());
                }
            }
        }
        return databases;
    }

    // 未测试版
    public static void dropDatabase(String dbName){
        File folder = new File("../TestData"+dbName);
        if(!folder.exists()||!folder.isDirectory()){
            System.out.println("not exist");
        }
        deleteDir(folder);
        System.out.println("Database "+dbName+" deleted successfully.");
    }

    private static void deleteDir(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files!=null){
                for(File f:files){
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }
}
