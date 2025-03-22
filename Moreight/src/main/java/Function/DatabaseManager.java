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
        File folder =  new File("../TestData/DatabaseManager/" + dbName);

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

    public static boolean useDatabase(String dbName){
        File folder =  new File("../TestData/DatabaseManager/" + dbName);
        if(!folder.exists()){
            return false;
        } else {
            currentDatabase = dbName;
            return true;
        }
    }

    public static List<String> showDatabases(){
        File folder =  new File("../TestData/DatabaseManager");
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

    public static void dropDatabase(String dbName,int userLevel){
        if(userLevel==1){return;}
        File folder = new File("../TestData/DatabaseManager" + dbName);
        if(!folder.exists()||!folder.isDirectory()){
            System.out.println("not exist");
            return;
        }
        if(deleteDir(folder)){
            System.out.println("Database "+dbName+" deleted successfully.");
        }else{
            System.out.println("Failed to delete database "+ dbName);
        }
    }

    // 递归调用，删除文件夹里的文件
    private static boolean deleteDir(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files!=null){
                for(File f:files){
                    if(!deleteDir(f)){
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}
