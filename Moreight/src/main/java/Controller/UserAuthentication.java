package Controller;

import User.User;
import User.UserManager;
import java.util.List;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.regex.Matcher;

public class UserAuthentication {
    // 授予权限语句: GRANT [权限级别] TO [用户名]
    public static boolean quit=false;
    private static final Pattern PATTERN_GRANT =
            Pattern.compile("(?i)^\\s*GRANT\\s+" +
                    "(admin|user|visitor)\\s+" +  // 权限级别
                    "TO\\s+" +
                    "(\\w+)\\s*;?\\s*$");        // 用户名

    // 收回权限语句: REVOKE [权限级别] FROM [用户名]
    private static final Pattern PATTERN_REVOKE =
            Pattern.compile("(?i)^\\s*REVOKE\\s+" +
                    "(admin|user|visitor)\\s+" +  // 权限级别
                    "FROM\\s+" +
                    "(\\w+)\\s*;?\\s*$");         // 用户名

    // 创建用户语句: CREATE USER [用户名] WITH PASSWORD '[密码]' [LEVEL [权限级别]]
    private static final Pattern PATTERN_CREATE_USER =
            Pattern.compile("(?i)^\\s*CREATE\\s+USER\\s+" +
                    "(\\w+)\\s+" +                // 用户名
                    "WITH\\s+PASSWORD\\s+'([^']+)'\\s+" +  // 密码
                    "(?:LEVEL\\s+(admin|user|visitor))?\\s*;?\\s*$");  // 可选权限级别

    // 修改用户权限语句: ALTER USER [用户名] SET LEVEL [权限级别]
    private static final Pattern PATTERN_ALTER_USER =
            Pattern.compile("(?i)^\\s*ALTER\\s+USER\\s+" +
                    "(\\w+)\\s+" +               // 用户名
                    "SET\\s+LEVEL\\s+" +
                    "(admin|user|visitor)\\s*;?\\s*$");  // 新权限级别

    // 删除用户语句: DROP USER [用户名]
    private static final Pattern PATTERN_DROP_USER =
            Pattern.compile("(?i)^\\s*DROP\\s+USER\\s+" +
                    "(\\w+)\\s*;?\\s*$");         // 用户名

    // 查看用户权限语句: SHOW PRIVILEGES [FOR USER [用户名]]
    private static final Pattern PATTERN_SHOW_PRIVILEGES =
            Pattern.compile("(?i)^\\s*SHOW\\s+PRIVILEGES\\s+" +
                    "(?:FOR\\s+USER\\s+(\\w+))?\\s*;?\\s*$");  // 可选用户名

    public static boolean login(Scanner sc) {
        System.out.print("用户名：");
        String username = sc.nextLine();
        System.out.print("密码：");
        String password = sc.nextLine();
        int result= UserManager.checkUserExists(username,password);
        String level=UserManager.getUserLevel(username,password);
        if(result==1){
            System.out.println("user name not exist!");
        }else if(result==2){
            System.out.println("password is not correct!");
        }else if(result==3){

            System.out.println("login successful! welcome "+username);

            UserManager.SetCurrentUser(new User(username,password,level));
            System.out.println(UserManager.getCurrentUser());
            return true;
        }else{
            System.out.println("error.exiting......");
        }
        // 在此实现登录逻辑
        return false;

    }

    public static boolean login1(String sc1,String sc2) {
        System.out.print("用户名：");
        String username = sc1;
        System.out.print("密码：");
        String password = sc2;
        int result= UserManager.checkUserExists(username,password);
        String level=UserManager.getUserLevel(username,password);
        if(result==1){
            System.out.println("user name not exist!");
        }else if(result==2){
            System.out.println("password is not correct!");
        }else if(result==3){

            System.out.println("login successful! welcome "+username);

            UserManager.SetCurrentUser(new User(username,password,level));
            System.out.println(UserManager.getCurrentUser());
            return true;
        }else{
            System.out.println("error.exiting......");
        }
        // 在此实现登录逻辑
        return false;

    }

    public static  boolean register(Scanner sc)  {
        System.out.print("设置用户名：");
        String username = sc.nextLine();
        System.out.print("设置密码：");
        String password = sc.nextLine();
        System.out.println("设置权限：");
        String level=sc.nextLine();
        int result = UserManager.CreateUser(username,password,level);
        if(result==2){



            System.out.println("login successful! welcome "+username);
            UserManager.SetCurrentUser(new User(username,password,level));


        }else if(result==3){
            System.out.println("valid levels are : admin|user|visitor,please check!");
        }
        else if(result==0){
            System.out.println("register failed ,please check !");

        }else{
            System.out.println("you have already registered, please log in!");

        }
        // 在此实现注册逻辑
        return false;
    }

    public static  boolean register1(String sc1,String sc2,String sc3)  {
        System.out.print("设置用户名：");
        String username = sc1;
        System.out.print("设置密码：");
        String password = sc2;
        System.out.println("设置权限：");
        String level=sc3;
        int result = UserManager.CreateUser(username,password,level);
        if(result==2){

            System.out.println("login successful! welcome "+username);
            UserManager.SetCurrentUser(new User(username,password,level));


        }else if(result==3){
            System.out.println("valid levels are : admin|user|visitor,please check!");
        }
        else if(result==0){
            System.out.println("register failed ,please check !");

        }else{
            System.out.println("you have already registered, please log in!");

        }
        // 在此实现注册逻辑
        return false;
    }


    public static void permissionManagement(Scanner sc) {
        // 检查登录状态d
        if (UserManager.GetCurrentUser() == null) {
            System.out.println("请先登录");
            return;
        }

        // 检查管理员权限
        if (!UserManager.GetCurrentUser().hasPermission("admin")) {
            System.out.println("权限不足，只有管理员可以管理权限");
            return;
        }


        String cmd;

        while (!"5".equals(cmd = sc.nextLine()) ) {
            System.out.println("\n=== 权限管理 ===");
            System.out.println("1. 授予/提升权限");
            System.out.println("2. 收回/降低权限");
            System.out.println("3. 查看所有用户");
            System.out.println("4. 删除用户");
            System.out.println("5. 返回主菜单");
            System.out.print("请选择操作: ");
            boolean matched = false;  // 标记是否匹配成功
            Matcher matcherGrant = PATTERN_GRANT.matcher(cmd);
            Matcher matcherRevoke = PATTERN_REVOKE.matcher(cmd);
            Matcher matcherDrop = PATTERN_DROP_USER.matcher(cmd);
            Matcher matcherShow=PATTERN_SHOW_PRIVILEGES.matcher(cmd);

            String choice = sc.nextLine().trim();

            if(matcherDrop.matches()){
                handleDeleteUser(sc);
                UserManager.updateUserFile();

            }else if(matcherGrant.matches()){

                handleGrantPermission(sc);
                UserManager.updateUserFile();

            }else if(matcherRevoke.matches()){
                handleRevokePermission(sc);
                UserManager.updateUserFile();

            }else if(matcherShow.matches()){
                displayAllUsers();

            }
            switch (choice) {
                case "1":
                    handleGrantPermission(sc);
                    UserManager.updateUserFile();
                    break;
                case "2":
                    handleRevokePermission(sc);
                    UserManager.updateUserFile();
                    break;
                case "3":
                    displayAllUsers();
                    break;
                case "4":
                    handleDeleteUser(sc);
                    UserManager.updateUserFile();
                    break;
                case "5":
                    quit=true;
                    return;
                default:
                    System.out.println("无效的选择，请输入1-5");
            }
        }
    }

    private static void handleGrantPermission(Scanner sc) {
        System.out.println("\n=== 授予/提升权限 ===");
        System.out.print("输入要修改权限的用户名: ");
        String username = sc.nextLine().trim();

        System.out.print("输入新的权限级别(admin/user/visitor): ");
        String level = sc.nextLine().trim().toLowerCase();

        boolean success = UserManager.grantPermission(username, level);
        System.out.println(success ? "操作成功" : "操作失败");
    }

    private static void handleRevokePermission(Scanner sc) {
        System.out.println("\n=== 收回/降低权限 ===");
        System.out.print("输入要修改权限的用户名: ");
        String username = sc.nextLine().trim();

        System.out.print("输入新的权限级别(user/visitor): ");
        String level = sc.nextLine().trim().toLowerCase();

        boolean success = UserManager.revokePermission(username, level);
        System.out.println(success ? "操作成功" : "操作失败");
    }
    private static void handleDeleteUser(Scanner sc) {
        System.out.println("\n=== 删除用户 ===");
        System.out.print("输入要删除的用户名: ");
        String username = sc.nextLine().trim();

        boolean success=UserManager.dropUser(username);

        System.out.println(success ? "操作成功" : "操作失败");
    }

    private static void displayAllUsers() {
        System.out.println("\n=== 所有用户列表 ===");
        System.out.printf("%-15s %-10s\n", "用户名", "权限级别");

        List<User> users = UserManager.getUsers();
        if(users.isEmpty()){
            System.out.println("loading.....");
            users=UserManager.getAllUsers();

        }
        if (users.isEmpty()) {
            System.out.println("没有用户数据");
            return;
        }

        for (User user : users) {
            System.out.printf("%-15s %-10s\n",
                    user.getUserName(),
                    user.getLevel());
        }
    }
}


