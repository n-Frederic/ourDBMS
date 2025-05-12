package User;

import java.io.*;

import com.google.gson.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;


import com.google.gson.stream.JsonReader;

/**
 * UserManager类用于管理用户信息。
 * 它支持用户创建和用户验证功能，用户信息存储在JSON文件中。
 */
public class UserManager {


    protected static User currentUser;
    protected static List<User> users=new ArrayList<>();

    /**
     * 获取当前登录的用户。
     * @return 当前登录的用户，如果未登录返回null。
     */
    public static User GetCurrentUser(){
        return currentUser;
    }

    public static void SetCurrentUser(User current){
        currentUser=current;
    }
    /**
     * 创建新用户。

     * @return 返回创建结果：
     *         0：失败，
     *         1：用户名已存在，
     *         2：成功。
     */

    public static User getUser(String username) {
        if(users.equals(null)) {
            System.out.println("getting users no user");
            users=getAllUsers();
        }

        for (User user : users) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }
    public static int CreateUser(String user, String password,String level) {
        JsonObject newUser = new JsonObject();
        newUser.addProperty("userName", user);
        newUser.addProperty("password", password);
        newUser.addProperty("level",level);
        //usersInfo.put(user, new User(user, password, 0));

        File file = new File("../TestData/UserManager/UserManager.json");

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonArray userArray = JsonParser.parseReader(reader).getAsJsonArray();
                int check = checkUserExists(userArray,user,password);
                boolean validLevel=isValidLevel(level);

                if(check == 1&&validLevel) {
                    userArray.add(newUser);
                    FileWriter writer = new FileWriter(file);
                    Gson gson = new Gson();
                    writer.write(gson.toJson(userArray));
                    writer.flush();
                    writer.close();
                    return 2;
                } else if (check == 2 || check == 3) {
                    return 1;
                } else if(!validLevel) return 3;
                else return 0;
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



    /**
     * 验证用户是否存在。
     * @param userName 用户名。
     * @param password 密码。
     * @return 返回验证结果：
     *         0：其他错误，
     *         1：未找到用户，
     *         2：密码错误，
     *         3：用户和密码匹配。
     */
    public static int checkUserExists(String userName, String password) {
        File file = new File("../TestData/UserManager/UserManager.json");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // 处理可能的 BOM
            String jsonString = sb.toString().replace("\uFEFF", "");
            System.out.println("读取到的 JSON：" + jsonString);

            JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
            JsonArray userArray = JsonParser.parseReader(jsonReader).getAsJsonArray();

            return checkUserExists(userArray, userName, password);
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static String getUserLevel(String userName,String userPassword){
        File file = new File("../TestData/UserManager/UserManager.json");

        // 检查文件是否存在
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            // 读取JSON文件内容
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // 处理可能的BOM头
            String jsonString = sb.toString().replace("\uFEFF", "");

            // 解析JSON数组
            JsonArray userArray = JsonParser.parseString(jsonString).getAsJsonArray();

            // 遍历用户数组查找匹配的用户
            for (JsonElement userElement : userArray) {
                JsonObject user = userElement.getAsJsonObject();
                String storedUsername = user.get("userName").getAsString();
                String storedPassword = user.get("password").getAsString();

                // 验证用户名和密码
                if (storedUsername.equals(userName) && storedPassword.equals(userPassword)) {
                    // 返回用户等级
                    return user.get("level").getAsString();
                }
            }

            // 没有找到匹配的用户
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 将当前用户列表更新到文件
     * @return 操作是否成功
     */
    public static boolean updateUserFile() {
        File file = new File("../TestData/UserManager/UserManager.json");
        JsonArray userArray = new JsonArray();

        if(users.isEmpty()||users.equals(null))return false;
        // 转换用户列表为JSON数组
        for (User user : users) {
            JsonObject userObj = new JsonObject();
            userObj.addProperty("userName", user.getUserName());
            userObj.addProperty("password", user.getPassword());
            userObj.addProperty("level", user.getLevel());
            userArray.add(userObj);
        }

        // 写入文件
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new Gson();
            writer.write(gson.toJson(userArray));
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 在用户数组中查找用户。
     * @param userArray 用户数组。
     * @param userName 用户名。
     * @param password 密码。
     * @return 返回验证结果：
     *         3：用户和密码匹配，
     *         2：用户名存在但密码错误，
     *         1：用户名不存在。
     */
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


    public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        File file = new File("../TestData/UserManager/UserManager.json");

        if (!file.exists()) {
            System.out.println("file not exist!");
            return userList;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            // 读取JSON文件内容
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // 处理可能的BOM头
            String jsonString = sb.toString().replace("\uFEFF", "");

            // 解析JSON数组
            JsonArray userArray = JsonParser.parseString(jsonString).getAsJsonArray();

            // 转换为User对象列表
            for (JsonElement userElement : userArray) {
                JsonObject userObj = userElement.getAsJsonObject();
                System.out.println( userObj.get("userName").getAsString());
                User user = new User(
                        userObj.get("userName").getAsString(),
                        userObj.get("password").getAsString(),
                        userObj.get("level").getAsString()
                );
                userList.add(user);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        return userList;
    }

    public static User getCurrentUser(){
        return currentUser;
    }
    public static List<User> getUsers(){
        return users;
    }

    // 授予用户权限
    public static boolean grantPermission(String targetUsername, String newLevel) {
        // 检查当前用户是否有权限执行此操作
        if (currentUser == null || !currentUser.hasPermission("admin")) {
            System.out.println("权限不足，只有管理员可以授予权限");
            return false;
        }
        // 不能修改自己的权限
        if (targetUsername.equals(currentUser.getUserName())) {
            System.out.println("不能修改自己的权限级别");
            return false;
        }
        System.out.println(users);

        if (users.isEmpty()) {
            users=UserManager.getAllUsers();
            System.out.println(users);
        }


        User targetUser =UserManager.getUser(targetUsername);
        if (targetUser == null) {
            System.out.println(users);
            System.out.println("用户不存在");
            return false;
        }

        // 验证新权限级别是否有效
        if (!isValidLevel(newLevel)) {
            System.out.println("无效的权限级别");
            return false;
        }

        targetUser.setLevel(newLevel);
        System.out.println("已成功将用户 " + targetUsername + " 的权限级别更改为 " + newLevel);
        return true;
    }


    // 收回用户权限（降级）
    public static boolean revokePermission(String targetUsername, String newLevel) {
        // 检查当前用户是否有权限执行此操作
        if (currentUser == null || !currentUser.hasPermission("admin")) {
            System.out.println("权限不足，只有管理员可以授予权限");
            return false;
        }
        // 不能修改自己的权限
        if (targetUsername.equals(currentUser.getUserName())) {
            System.out.println("不能修改自己的权限级别");
            return false;
        }
        System.out.println(users);

        if (users.isEmpty()) {
            users=UserManager.getAllUsers();
            System.out.println(users);
        }


        User targetUser =UserManager.getUser(targetUsername);
        if (targetUser == null) {
            System.out.println(users);
            System.out.println("用户不存在");
            return false;
        }

        // 验证新权限级别是否有效
        if (!isValidLevel(newLevel)) {
            System.out.println("无效的权限级别");
            return false;
        }


        // 不能修改自己的权限
        if (targetUsername.equals(currentUser.getUserName())) {
            System.out.println("不能修改自己的权限级别");
            return false;
        }

        targetUser.setLevel(newLevel);
        UserManager.getUser(targetUsername).setLevel(newLevel);
        System.out.println("已成功将用户 " + targetUsername + " 的权限级别更改为 " + newLevel);

        for (User user : users) {
            System.out.printf("%-15s %-10s\n",
                    user.getUserName(),
                    user.getLevel());
        }
        return true;
    }

    // 验证权限级别是否有效
    private static boolean isValidLevel(String level) {
        return level.equals("admin") || level.equals("user") || level.equals("visitor");
    }

}


