package Funtion;

import java.io.*;
import java.util.*;

public class CreateDatabase {

    public static void CreateDataBase(String user, String database) {
        File folder =  new File("../TestData/" + user + "/" + database);

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

    public static List<String> showDatabases(String user){
        File userFolder =  new File("../TestData/" + user);
        List<String> databases = new ArrayList<>();

        if(!userFolder.exists()||!userFolder.isDirectory()){
            System.out.println("User directory does not exist.");
        }
        File[] files = userFolder.listFiles();
        if(files!=null){
            for(File file:files){
                if(file.isDirectory()){
                    databases.add(file.getName());
                }
            }
        }
        return databases;
    }
}
