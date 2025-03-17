package Function;

import java.io.*;

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


}
