package Function;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowDatabase {
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
