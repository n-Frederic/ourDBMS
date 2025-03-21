package Function;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserManager {
    protected static User currentUser;
    // 存放用户信息。后期可以每次运行时把json文件的数据先缓存到哈希表里，
    // checkUserExists从哈希表里读写效率更高，
    // 每次只有创建新用户的时候才进行json读写。
    //private static Map<String, User> usersInfo = new HashMap<>();

    // 0是失败 1是重名 2是成功
    public static int CreateUser(String user, String password) {
        JsonObject newUser = new JsonObject();
        newUser.addProperty("userName", user);
        newUser.addProperty("password", password);
        //usersInfo.put(user, new User(user, password, 0));

        File file = new File("../TestData/UserManager/UserManager.json");

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonArray userArray = JsonParser.parseReader(reader).getAsJsonArray();
                int check = checkUserExists(userArray,user,password);
                if(check == 1) {
                    userArray.add(newUser);
                    FileWriter writer = new FileWriter(file);
                    Gson gson = new Gson();
                    writer.write(gson.toJson(userArray));
                    writer.flush();
                    writer.close();
                    return 2;
                } else if (check == 2 || check == 3) {
                    return 1;
                } else return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            JsonArray userArray = new JsonArray();
            userArray.add(newUser);

            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new Gson();
                writer.write(gson.toJson(userArray));
                writer.flush();
                writer.close();
                return 2;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }


    // 0是存在其他错误 1是没找到该用户 2是密码错误 3是用户，密码都匹配的上
    public static int checkUserExists(String userName, String password) {
        File file = new File("../TestData/UserManager/UserManager.json");
        try (FileReader reader = new FileReader(file)) {
            JsonArray userArray = JsonParser.parseReader(reader).getAsJsonArray();
            return checkUserExists(userArray, userName, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int checkUserExists(JsonArray userArray, String userName, String password) {
        for (int i = 0; i < userArray.size(); i++) {
            JsonObject user = userArray.get(i).getAsJsonObject();
            String storedUsername = user.get("userName").getAsString();
            String storedPassword = user.get("password").getAsString();

            if (storedUsername.equals(userName) && storedPassword.equals(password)) {
                return 3;
            } else if (storedUsername.equals(userName)) {
                return 2;
            }
        }
        return 1;
    }


}
