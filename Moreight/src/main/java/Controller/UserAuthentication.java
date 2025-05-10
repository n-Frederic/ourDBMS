package Controller;

import User.User;
import User.UserManager;

import java.util.Scanner;

public class UserAuthentication {

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

}
