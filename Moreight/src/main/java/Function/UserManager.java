package Function;

import java.io.File;

public class UserManager {
    protected static User currentUser;

    public static String CreateUser(String user,String password) {
        File folder =  new File("../TestData/" + user);

        if(!folder.exists()) {
            if(folder.mkdirs()) {
                return "CreateUser Successfully : " + folder.getAbsolutePath();
            } else return "CreateUser failed.";
        } else return "The user has already existed : " + folder.getAbsolutePath();
    }

    public static String GetUser(String user,String password) {
        File folder =  new File("../TestData/" + user);
        if(!folder.exists()) {
            if(folder.mkdirs()) {
                return "CreateUser Successfully : " + folder.getAbsolutePath();
            } else return "CreateUser failed.";
        } else return "The user has already existed : " + folder.getAbsolutePath();
    }


}
