package Funtion;

import java.io.File;

public class CreateUser {
    public static void CreateUser(String user) {
        File folder =  new File("../TestData/" + user);

        if(!folder.exists()) {
            if(folder.mkdirs()) {
                System.out.println("CreateUser Successfully : " + folder.getAbsolutePath());
            } else {
                System.out.println("CreateUser failed.");
            }
        } else {
            System.out.println("The user has already existed : " + folder.getAbsolutePath());
        }
    }
}
